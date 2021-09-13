package fr.uni.prjpidgeon

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fr.uni.prjpidgeon.databinding.ActivityMainBinding
import kotlin.math.*
import kotlin.math.atan
import android.R

import android.view.animation.Animation
import android.view.animation.AnimationUtils


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding


    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private var estPitch = 0f
    private var estRoll = 0f
    private var estYaw = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            estPitch = asin(event.values[0]/9.81f)
            estRoll = atan(event.values[1]/event.values[2])

            binding.pitch.text = estPitch.toString()
            binding.roll.text = estRoll.toString()

            binding.xAxis.text = event.values[0].toString()
            binding.yAxis.text = event.values[1].toString()
            binding.zAxis.text = event.values[2].toString()

            ObjectAnimator.ofFloat(
                binding.centerCross,
                "translationX",
                + estPitch * 200
            ).apply { start() }

            ObjectAnimator.ofFloat(
                binding.centerCross,
                "translationY",
                - estRoll * 200
            ).apply { start() }

        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val xmag = event.values[0]
            val ymag = event.values[1]
            val zmag = event.values[2]

            estYaw = atan2(-ymag, xmag)
            /*estYaw =  when {
                xmag < 0f -> (180f - atan(xmag / ymag))
                xmag > 0f && ymag < 0f -> -atan(ymag/xmag)
                xmag > 0f && ymag > 0f -> 360f - atan(ymag/xmag)
                xmag == 0f && ymag < 0f -> 90f
                else -> 270f
            }*/

            binding.yaw.text = estYaw.toString()

            ObjectAnimator.ofFloat(
                binding.compass,
                "rotation",
                -(estYaw / PI * 180f).toFloat()).apply { start() }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }


}