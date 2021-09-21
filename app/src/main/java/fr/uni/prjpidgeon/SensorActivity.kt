package fr.uni.prjpidgeon

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.uni.prjpidgeon.databinding.ActivityMainBinding
import kotlin.math.*

abstract class SensorActivity: AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var estPitch = 0f
    private var estRoll = 0f
    private var estYaw = 0f

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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            estPitch = - asin(event.values[0]/9.81f)
            estRoll = - atan(event.values[1]/event.values[2])

            onPitchChanged(estPitch)
            onRollChanged(estRoll)
            onAcelerometerChanged(event.values)

        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            val xmag = event.values[0] / 1000
            val ymag = event.values[1] / 1000
            val zmag = event.values[2] / 1000

            onAcelerometerChanged(event.values)

            val a = cos(estRoll)*ymag - sin(estRoll)*zmag
            val b = cos(estPitch)*xmag + sin(estRoll)*sin(estPitch)*ymag
            + cos(estRoll)*sin(estPitch)*zmag
            var estYawm = atan2(a,b)
            estYaw = D - estYawm

            val yawDeg = Math.toDegrees(estYaw.toDouble()).toFloat()
            onYawChanged(yawDeg, isCalibrated)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    abstract fun onPitchChanged(pitch: Float)
    abstract fun onRollChanged(pitch: Float)
    abstract fun onYawChanged(pitch: Float, isCalibrated:Boolean)

    abstract fun onAcelerometerChanged(acc: FloatArray)

    companion object {
        const val TAG = "SensorActivity"
        const val D = 0.04416F

    }

}