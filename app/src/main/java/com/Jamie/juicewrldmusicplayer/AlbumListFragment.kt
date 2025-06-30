package com.Jamie.juicewrldmusicplayer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Jamie.juicewrldmusicplayer.HomeFragment.Companion.randomAlbum
import com.Jamie.juicewrldmusicplayer.databinding.FragmentAlbumListBinding




class AlbumListFragment : Fragment() {
    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!

    private lateinit var albumListRecycler: RecyclerView
    private lateinit var dbHelper: DbHelper
    private lateinit var adapter: AlbumListRecyclerAdapter

    private lateinit var albumCount:TextView
    private lateinit var backButton:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlbumListBinding.inflate(inflater,container, false)
        init()
        return binding.root
    }

    private fun init(){
        albumListRecycler = binding.albumList
        albumListRecycler.layoutManager = GridLayoutManager(requireContext(),2)
        dbHelper = DbHelper(this.requireContext())
        val albums = dbHelper.getAllAlbums()


        adapter = AlbumListRecyclerAdapter(requireContext(), albums) { album ->
            val fragment = AlbumFragment().apply { // Use a different fragment if needed
                arguments = Bundle().apply {
                    putInt("albumId", album.id)
                    Log.d(TAG, "init: The album id is ${album.id}")
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        albumListRecycler.adapter = adapter



        albumCount = binding.albumCount
        albumCount.text = "${albums.size} Albums"

        backButton = binding.backButton
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val TAG = "AlbumListFragment"
    }
}