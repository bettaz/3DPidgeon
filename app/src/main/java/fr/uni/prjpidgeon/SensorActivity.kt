package fr.uni.prjpidgeon

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.uni.prjpidgeon.databinding.ActivityMainBinding
import kotlin.math.*

abstract class SensorActivity: AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private var estPitch = 0f
    private var estRoll = 0f
    private var estYaw = 0f
    private var xmag = 0f;
    private var ymag = 0f;
    private var zmag = 0f;


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
        if (currentTime - lastUpdateTime >= 150) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                val D = 0.04416F
                estPitch = asin(event.values[0]/9.81f)
                estRoll = atan(event.values[1]/event.values[2])
                estYaw = D - atan((cos(estRoll)*ymag - sin(estRoll)*zmag)
                        / (cos(estPitch)*xmag + sin(estRoll)*sin(estPitch)*ymag
                        + cos(estRoll)*sin(estPitch)*zmag))

                onAcelerometerChanged(event.values)
                onPitchChanged(estPitch)
                onRollChanged(estRoll)
                onYawChanged(estYaw)

            }
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {

                xmag = event.values[0]
                ymag = event.values[1]
                zmag = event.values[2]
            }
            lastUpdateTime = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    abstract fun onPitchChanged(pitch: Float)
    abstract fun onRollChanged(pitch: Float)
    abstract fun onYawChanged(pitch: Float)

    abstract fun onAcelerometerChanged(acc: FloatArray)

}