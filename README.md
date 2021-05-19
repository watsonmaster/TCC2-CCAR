# TCC2-CCAR
Repositório do App de Realidade Aumentada do TCC2 da Universidade Federal da Fronteira Sul

Linguagem **Kotlin**
Plataforma Android
SDK [Google ARCore](https://developers.google.com/ar)
[Sceneform](https://developers.google.com/sceneform/develop) SDK 1.5


**Requisitos:**

Android Studio 3.6
[Download Windows 64 bits](https://redirector.gvt1.com/edgedl/android/studio/ide-zips/3.6.1.0/android-studio-ide-192.6241897-windows.zip) 
[Download Windows 32 bits](https://redirector.gvt1.com/edgedl/android/studio/ide-zips/3.6.1.0/android-studio-ide-192.6241897-windows32.zip)

Para o uso no emulador é necessário instalar o Google CoreAR, disponível na Google PlayStore, caso deseje instalar manualmente o APK pode baixar neste link:
[ARCore SDK for Android](https://github.com/google-ar/arcore-android-sdk/releases)

## Inserindo novos Modelos 3D

Para adicionar modelos eles precisam estar no formato obj e acompanhar o arquivo mtl para que o sceneform possa converter e disponibilizar o uso no App.

Os modelos devem estar na pasta: **app\sampledata\models**
E declarados no build.graddle e na lista dentro da Classe ArView
**build.graddle**

    sceneform.asset(  
        'sampledata/models/objeto.obj',  
        'default',  
        'sampledata/models/objeto.sfa',  
        'src/main/res/raw/objeto'  
        )
**class ArView**

    private val models = mutableListOf(  
        Model(R.drawable.objeto, "Objeto", R.raw.objeto)
    )