package br.edu.uffs.cc.arcc

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar_view.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.concurrent.CompletableFuture

private const val BOTTOM_SHEET_PEEK_HEIGHT = 50f


class ArView : AppCompatActivity() {

    //Lista de Modelos a serem carregados
    private val models = mutableListOf(
        Model(R.drawable.cranio, "Cranio", R.raw.cranio),
        Model(R.drawable.skeleton, "Esqueleto", R.raw.skeleton),
        Model(R.drawable.heart, "Coração", R.raw.heartbase)
    )

    lateinit var arFrament: ArFragment

    val viewNodes = mutableListOf<Node>()

    private lateinit var selectedModel: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_view)
        arFrament = frament as  ArFragment
        setupBottomSheet()
        setupRecyclerView()

        setupDoubleTapArPlaneListener()
        getCurrentScene().addOnUpdateListener {
            rotateViewNodesTowardsUser()
        }


    }

    private fun setupDoubleTapArPlaneListener(){
        arFrament.setOnTapArPlaneListener { hitResult, _, _ ->
            loadModel { modelRenderable, viewRenderable ->
                addNoteToScene(hitResult.createAnchor(), modelRenderable, viewRenderable)
            }
        }
    }

    private fun setupRecyclerView() {
        rvModels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvModels.adapter = ModelAdapter(models).apply {
            selectedModel.observe(this@ArView, Observer {
                this@ArView.selectedModel = it
                val newTitle = "Modelos (${it.title})"
                tvModel.text = newTitle
            })
        }
    }


    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        try {
            bottomSheetBehavior.peekHeight =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    BOTTOM_SHEET_PEEK_HEIGHT,
                    resources.displayMetrics
                ).toInt()

            bottomSheetBehavior.addBottomSheetCallback(object:
                BottomSheetBehavior.BottomSheetCallback()  {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    bottomSheet.bringToFront()
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }
            })

        }
        catch (e: Exception){
            Toast.makeText(this,"Error $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun getCurrentScene() = arFrament.arSceneView.scene



    private fun createDeleteButton(): Button {
        return Button(this).apply {
            text = "Remover"
            setBackgroundColor(ContextCompat.getColor(this@ArView, R.color.GreenUFFSDark))
            setTextColor(ContextCompat.getColor(this@ArView, R.color.WhiteSoft))
        }
    }

    private fun rotateViewNodesTowardsUser(){
        for (node in viewNodes){
            node.renderable?.let {
                val camPos = getCurrentScene().camera.worldPosition
                val viewNodePos = node.worldPosition
                val dir = Vector3.subtract(camPos, viewNodePos)
                node.worldRotation = Quaternion.lookRotation(dir, Vector3.up())
            }
        }
    }

    private fun addNoteToScene(
        anchor: Anchor,
        modelRenderable: ModelRenderable,
        viewRenderable: ViewRenderable
    ) {
        val anchorNode = AnchorNode(anchor)
        val modelNote = TransformableNode(arFrament.transformationSystem).apply {
            renderable = modelRenderable

            //Teste de Escala - Deu certo
            scaleController.maxScale = 1.5f
            scaleController.minScale = 0.1f
            setParent(anchorNode)
            getCurrentScene().addChild(anchorNode)
            select()
        }
        val viewNode = Node().apply {
            renderable = null
            setParent(modelNote)
            val box = modelNote.renderable?.collisionShape as Box
            localPosition = Vector3(0f, box.size.y, 0f)
            (viewRenderable.view as Button).setOnClickListener {
                getCurrentScene().removeChild(anchorNode)
                viewNodes.remove(this)
            }
        }
        viewNodes.add(viewNode)
        modelNote.setOnTapListener { _, _  ->
            if(!modelNote.isTransforming){
                if(viewNode.renderable == null){
                    viewNode.renderable = viewRenderable
                } else {
                    viewNode.renderable = null
                }
            }
        }
    }

    private fun loadModel(callback: (ModelRenderable, ViewRenderable) -> Unit) {
        val modelRenderable = ModelRenderable.builder()
            .setSource(this, selectedModel.modelResourceId)
            .build()
        val viewRenderable = ViewRenderable.builder()
            .setView(this, createDeleteButton())
            .build()
        CompletableFuture.allOf(modelRenderable, viewRenderable)
            .thenAccept {
                callback(modelRenderable.get(), viewRenderable.get())
            }
            .exceptionally {
                Toast.makeText(this, "Error loading model: $it", Toast.LENGTH_LONG).show()
                null
            }
    }
}
