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


class MainActivity : SensorActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    override fun onPitchChanged(pitch: Float) {
        binding.pitch.text = pitch.toString()

        ObjectAnimator.ofFloat(
            binding.centerCross,
            "translationX",
            if(pitch.isFinite()) pitch * 200 else 200f
        ).apply { start() }
    }

    override fun onRollChanged(roll: Float) {
        binding.roll.text = roll.toString()

        ObjectAnimator.ofFloat(
            binding.centerCross,
            "translationY",
            if(roll.isFinite()) - roll * 200 else 200f
        ).apply { start() }
    }

    override fun onYawChanged(yaw: Float) {
        binding.yaw.text = yaw.toString()

        ObjectAnimator.ofFloat(
            binding.compass,
            "rotation",
            -(yaw / PI * 180f).toFloat()
        ).apply { start() }
    }

    override fun onAcelerometerChanged(acc: FloatArray) {
        binding.xAxis.text = acc[0].toString()
        binding.xAxis.text = acc[1].toString()
        binding.xAxis.text = acc[2].toString()
    }

}