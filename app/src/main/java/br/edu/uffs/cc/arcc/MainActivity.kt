package br.edu.uffs.cc.arcc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_prox.setOnClickListener {
            val intent= Intent(this, ArView::class.java)
            startActivity(intent)
            //Método finish fecha a atividade atual ao trocar de tela
            //finish()
        }
    }
}
