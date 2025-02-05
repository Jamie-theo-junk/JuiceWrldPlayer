package com.Jamie.juicewrldmusicplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Jamie.juicewrldmusicplayer.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private var _binding:FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter:ListRecyclerAdapter
    private lateinit var songRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater,container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init(){
        songRecycler = binding.songListRecycler
        songRecycler.layoutManager = LinearLayoutManager(requireContext())
        val dbHelper = DbHelper(this.requireContext())
        val songs = dbHelper.getAllSongs()
        adapter = ListRecyclerAdapter(this.requireContext(),songs)
        songRecycler.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

