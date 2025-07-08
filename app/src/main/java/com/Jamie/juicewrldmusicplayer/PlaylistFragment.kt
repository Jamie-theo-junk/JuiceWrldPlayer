package com.Jamie.juicewrldmusicplayer

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.Jamie.juicewrldmusicplayer.databinding.FragmentAlbumBinding
import com.Jamie.juicewrldmusicplayer.databinding.FragmentPlaylistBinding


class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var backButton:ImageView
    private lateinit var createNewPlaylistBtn: RelativeLayout
    private lateinit var playlistRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlaylistBinding.inflate(inflater,container, false)
        init()
        return binding.root
    }

    private fun init(){
        backButton = binding.backButton
        createNewPlaylistBtn = binding.playlistBtn
        playlistRecyclerView = binding.playlistRecycler

        createNewPlaylistBtn
    }
//
//    private fun showPopup(context: Context, p: Point) {
//
//        val layoutInflater =
//            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//        val binding = PopupLayoutBinding.inflate(layoutInflater)
//        val popUp = PopupWindow(context)
//        popUp.contentView = binding.root
//        popUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
//        popUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        popUp.isFocusable = true
//
//        val x = 200
//        val y = 60
//        popUp.setBackgroundDrawable(ColorDrawable())
//        popUp.animationStyle = R.style.popup_window_animation
//        popUp.showAtLocation(binding.root, Gravity.NO_GRAVITY, p.x + x, p.y + y)
//
//        binding.btnPopupEdit.setOnClickListener {
//            popUp.dismiss()
//        }
//
//        binding.btnPopupDelete.setOnClickListener {
//            popUp.dismiss()
//
//        }
//
//    }

    companion object {

    }
}