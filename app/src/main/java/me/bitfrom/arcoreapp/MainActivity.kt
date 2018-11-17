package me.bitfrom.arcoreapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment

    private var selectedObject: Uri = Uri.parse("chair.sfb")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        initializeGallery()

        fragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            placeObject(fragment, hitResult.createAnchor(), selectedObject)
        }
    }

    private fun initializeGallery() {
        chair.setOnClickListener {
            selectedObject = Uri.parse("chair.sfb")
            printSelectedObject()
        }
        couch.setOnClickListener {
            selectedObject = Uri.parse("couch.sfb")
            printSelectedObject()
        }
        lamp.setOnClickListener {
            selectedObject = Uri.parse("lamp.sfb")
            printSelectedObject()
        }
    }

    private fun placeObject(arFragment: ArFragment, anchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept{ renderable -> addNoteToScene(arFragment, anchor, renderable) }
            .exceptionally {
                printError(it)
                return@exceptionally null
            }
    }

    private fun addNoteToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun printSelectedObject() {
        Toast.makeText(this, "Selected object: $selectedObject", Toast.LENGTH_SHORT).show()
    }

    private fun printError(throwable: Throwable) {
        Toast.makeText(this, "Error: $throwable", Toast.LENGTH_LONG).show()
    }
}
