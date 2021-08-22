package com.example.guesstheword.screens.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        viewModel.currentTime.observe(this, Observer { newTime ->
            binding.timerText.text = DateUtils.formatElapsedTime(newTime)
        })

        // set up event listening to navigate the player when the game is finished
        viewModel.eventGameFinish.observe(this, Observer { hasFinished ->
            if (hasFinished) {
                val currentScore = viewModel.score.value ?: 0
                val action = GameFragmentDirections.actionGameToScore(currentScore)
                findNavController().navigate(action)
                viewModel.onGameFinishComplete()
            }
        })

        return binding.root
    }
}
