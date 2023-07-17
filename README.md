## Pagination and Local Caching Using Paging3

### Technologies
* Android Studio
* Kotlin
* Jetpack Compose
* Retrofit
* Room
* Paging3
* MVVM
* Coil - to remotely load images

### Paging3
* Needed
  * Pager - viewmodel
    * to provide a flow for the loaded entries
    * newly loaded items
  * PagingSource - dao
  * RemoteMediator - mediator
  * PagingState - load()
  * LazyPagingItem - ui

### Data Flow
* Remote Data Source -- <API, DTO> --> Local Cache -- <ROOM, DAO> --> UI
  * purpose : Access to the data even if the app is offline
* get the data from api using pagination
  * at once, get 20 items
* to handle them(caching + paging) efficiently, use Paging3 library

### TODO
* implement a beer application
* show the information of each beer
  * image
  * title
  * desc
* paging when scrolled till the end
* even if the network is unconnected, data should be shown

### Initial Setup
* dependencies
* build.gradle(project)

      buildscript {
          ext {
              compose_ui_version = '1.4.0'
          }
          dependencies {
              classpath 'com.google.dagger:hilt-android-gradle-plugin:2.45'
          }
      }
      
      plugins {
          id 'com.android.application' version '8.1.0-beta05' apply false
          id 'org.jetbrains.kotlin.android' version '1.8.10' apply false
          id 'com.android.library' version '7.4.0' apply false
      }

* build.gradle(app)

      plugins {
          id 'com.android.application'
          id 'org.jetbrains.kotlin.android'
          id 'kotlin-kapt'
          id 'dagger.hilt.android.plugin'
      }

      dependencies {   

          // Coil Compose
          implementation 'io.coil-kt:coil-compose:2.4.0'
          
          // lifecycle
          implementation 'androidx.lifecycle:lifecycle-runtime-compose:2.6.1'
          implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1'
          
          // Dagger-Hilt
          implementation 'com.google.dagger:hilt-android:2.45'
          kapt 'com.google.dagger:hilt-android-compiler:2.45'
          kapt 'androidx.hilt:hilt-compiler:1.0.0'
          implementation 'androidx.hilt:hilt-navigation-compose:1.0.0'
          
          // paging3
          implementation "androidx.paging:paging-runtime-ktx:3.1.1"
          implementation "androidx.paging:paging-compose:1.0.0-alpha18"
          
          // Retrofit
          implementation 'com.squareup.retrofit2:retrofit:2.9.0'
          implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
          implementation "com.squareup.okhttp3:okhttp:5.0.0-alpha.2"
          implementation "com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2"
          
          // Room
          implementation 'androidx.room:room-ktx:2.5.2'
          kapt 'androidx.room:room-compiler:2.5.2'
          // room paging
          implementation 'androidx.room:room-paging:2.5.2'
      }

### Project Package
* data
  * remote : implement api interface with retrofit, database with room
    * BeerDto(Data Class) : network model... Data Transfer Object that represents the network model
    * BeerApi(Interface) : define the different end points 
      * const val 'base url'
      * get method to get beers info
    * BeerRemoteMediator(Class)
      * inherit from RemoteMediator()
      * Remote Mediator?
        * Central Control Unit controlling the paging logic
        * put loaded items from remote API into local database
        * forward the page we want to load
      * As a Mediator, it should be able to access to both of the data sources(api, database)
        * beer database(local database) : BeerDatabase
        * remote data source(remote api) : BeerApi
      * function : load(loadType: LoadType, state: PagingState<Int, BeerEntity>)
        * load() : called whenever there's some form of loading in regards to pagination
          * loadTypes : to distinguish different types of loading
            * refresh(whole list refresh... swipe to refresh), appending new items(scroll down for next data)
          * pagingState : current kind of paging config
        * returns MediatorResult : to tell the paging library how to load items from the api and tell which case is a success and which case is an error
  * local : local caching using database
    * BeerEntity(Data Class): database model... entity of database. the same with BeerDto. to cache from Api that contains the information in BeerDto
      * Annotate with @Entity
    * BeerDao(Interface)
      * Annotate with @Dao
      * upsert(get data from api and put into database)
      * select(조회... 모든 데이터 가져오기. ui에 띄우기 위해)
      * delete(refresh 발생 시 초기화를 위함)
    * BeerDatabase(Class)
      * Annotate with @Database
  * mappers : to map network model to database model, and to map database model to domain model for showing on ui
    * BeerDto.toBeerEntity() : function to map remote -> local
    * BeerEntity.toBeer() : function to map local -> domain
* domain : domain level classes
  * Beer(Data Class) : Data that will be acutally shown on ui

