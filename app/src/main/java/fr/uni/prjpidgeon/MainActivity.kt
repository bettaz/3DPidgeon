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
import android.graphics.drawable.GradientDrawable

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

    }

    override fun onOrientationChanged(orientation: FloatArray, androidOrientation: FloatArray, accReads: FloatArray) {
        binding.xAxis.text = accReads[0].toString()
        binding.yAxis.text = accReads[1].toString()
        binding.zAxis.text = accReads[2].toString()
        var androidRoll = Math.toDegrees(androidOrientation[1].toDouble()).toFloat()
        var myRoll = Math.toDegrees(orientation[0].toDouble()).toFloat()
        binding.roll.text = myRoll.toString()
        ObjectAnimator.ofFloat(
            binding.centerCross,
            "translationX",
            if(myRoll.isFinite()) myRoll/90 * -150 else 150f
        ).apply { start() }
        var androidPitch = (Math.toDegrees(androidOrientation[2].toDouble()).toFloat())
        var myPitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
        binding.pitch.text = myPitch.toString()
        ObjectAnimator.ofFloat(
            binding.centerCross,
            "translationY",
            if(myPitch.isFinite()) myPitch/90 * -150 else 150f
        ).apply { start() }
        var androidYaw = Math.toDegrees(androidOrientation[0].toDouble()).toFloat()
        var myYaw = Math.toDegrees(orientation[2].toDouble()).toFloat()
        var myYaw_uncalib = Math.toDegrees(orientation[3].toDouble()).toFloat()
        binding.yaw.text = myYaw.toString()
        if (androidYaw.isFinite()) {
            ObjectAnimator.ofFloat(
                binding.compass,
                "rotation",
                if(binding.calibratedSwitch.isChecked) -myYaw else -myYaw_uncalib
            ).apply { start() }
        }
        binding.rollDiff.text = (androidRoll-myRoll).toString()
        binding.pitchDiff.text = (androidPitch-myPitch).toString()
        binding.yawDiff.text = (androidYaw- myYaw).toString()
    }

}