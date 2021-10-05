package fr.uni.prjpidgeon

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*

abstract class SensorActivity: AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var last_acc_read: FloatArray
    private lateinit var last_magn_read: FloatArray
    private var D = 0.04416
    private var rotationMatrix = FloatArray(16)
    private var rotationMatrixRemapped = FloatArray(16)
    private var accMagOrientation = FloatArray(3)
    private var estPitch = 0f
    private var estRoll = 0f
    private var estYaw = 0f
    private var estYaw_uncalib = 0f
    private var xmag = 0f
    private var ymag = 0f
    private var zmag = 0f
    private var xmag_uncalib = 0f
    private var ymag_uncalib = 0f
    private var zmag_uncalib = 0f
    private var androidOrientation = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }
    var lastUpdateTime = System.currentTimeMillis()
    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            last_acc_read =event.values
            if(this::last_acc_read.isInitialized && this::last_magn_read.isInitialized){
                SensorManager.getRotationMatrix(rotationMatrix, null,last_acc_read,last_magn_read)
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_Y,
                    SensorManager.AXIS_MINUS_X,
                    rotationMatrixRemapped
                )
                androidOrientation = SensorManager.getOrientation(rotationMatrixRemapped, accMagOrientation)
            }
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD || event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            if(event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
                xmag = event.values[1] / 1000000
                ymag = event.values[0] / 1000000
                zmag = -event.values[2] / 1000000
                last_magn_read = event.values
            }
            else {
                xmag_uncalib = event.values[1] / 1000000
                ymag_uncalib = event.values[0] / 1000000
                zmag_uncalib = -event.values[2] / 1000000
            }
        }
        if (currentTime - lastUpdateTime >= 100) {
            if(this::last_acc_read.isInitialized){
                estPitch = ( asin(last_acc_read[1]/9.81f))
                estRoll= ( atan(last_acc_read[0]/-last_acc_read[2]))
                var a = cos(estRoll)*ymag - sin(estRoll)*zmag
                var b = cos(estPitch) * xmag + sin(estRoll) * sin(estPitch) * ymag + sin(estPitch) * cos(estRoll) * zmag
                estYaw =  Math.toRadians(D).toFloat() - atan2(a,b)
                var a_uncalib = cos(estRoll)*ymag_uncalib - sin(estRoll)*zmag_uncalib
                var b_uncalib = cos(estPitch) * xmag_uncalib + sin(estRoll) * sin(estPitch) * ymag_uncalib + sin(estPitch) * cos(estRoll) * zmag_uncalib
                estYaw_uncalib =  Math.toRadians(D).toFloat() - atan2(a_uncalib,b_uncalib)
                onOrientationChanged(floatArrayOf(estRoll,estPitch,estYaw,estYaw_uncalib),androidOrientation,last_acc_read)
                lastUpdateTime = currentTime
            }
        }
    }

    abstract fun onOrientationChanged(orientation: FloatArray, androidOrientation: FloatArray, accReads: FloatArray)

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    abstract fun onAcelerometerChanged(acc: FloatArray)

    companion object {
        const val TAG = "SensorActivity"
        const val D = 0.04416F

    }

}