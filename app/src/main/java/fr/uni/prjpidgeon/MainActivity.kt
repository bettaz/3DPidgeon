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

    override fun onAcelerometerChanged(acc: FloatArray) {
        binding.xAxis.text = acc[0].toString()
        binding.yAxis.text = acc[1].toString()
        binding.zAxis.text = acc[2].toString()
    }

    override fun onOrientationChanged(androidOrientation: FloatArray, orientation: FloatArray, accReads: FloatArray) {

        binding.roll.text = Math.toDegrees(-orientation[1].toDouble()).toString()
        ObjectAnimator.ofFloat(
            binding.centerCross,
            "translationY",
            if(orientation[2].isFinite()) sin(-orientation[2]) * 150 else 150f
        ).apply { start() }

        binding.pitch.text = ((Math.toDegrees(-orientation[2].toDouble())+180)%360).toString()
        ObjectAnimator.ofFloat(
            binding.centerCross,
            "translationX",
            if(orientation[1].isFinite()) sin(orientation[1]) * 150 else 150f
        ).apply { start() }

        if (orientation[0].isFinite()) {
            binding.yaw.text = (Math.toDegrees(orientation[0].toDouble())).toString()

            ObjectAnimator.ofFloat(
                binding.compass,
                "rotation",
                (Math.toDegrees(-orientation[0].toDouble())-90).toFloat()
            ).apply { start() }
        }
        binding.rollDiff.text = Math.toDegrees(((orientation[1]-androidOrientation[0]).toDouble())).toString()
        binding.pitchDiff.text = Math.toDegrees(((orientation[2]-androidOrientation[1]).toDouble())).toString()
        binding.yawDiff.text = Math.toDegrees(((orientation[0]-androidOrientation[2]).toDouble())).toString()
    }

}