package com.example.guesstheword.screens.game

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.guesstheword.R
import com.example.guesstheword.databinding.GameFragmentBinding

/**
 * The fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false)

        //Get the viewModel
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Set the viewModel for databinding - this allows the bound layout access to all of the the
        // data in the viewModel
        binding.gameViewModel = viewModel

        // Specify the current activity as the lifecycle owner of the binding
        // This is used so that the binding can observe LiveData updates
        binding.setLifecycleOwner(this)

        // Sets up event listening to navigate the player when the game is finished
        viewModel.eventGameFinish.observe(this, Observer { hasFinished ->
            if (hasFinished) {
                val currentScore = viewModel.score.value ?: 0
                val action = GameFragmentDirections.actionGameToScore(currentScore)
                findNavController().navigate(action)
                viewModel.onGameFinishComplete()
            }
        })

        // Buzzes when triggered with different buzz events
        viewModel.eventBuzz.observe(viewLifecycleOwner, Observer { buzzType ->
            if (buzzType != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(buzzType.pattern)
                viewModel.onBuzzComplete()
            }
        })

        return binding.root
    }

    /**Given a pattern, this method makes sure the device buzzes **/
    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                // Deprecated in API level 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }
}
