# Add project specific ProGuard rules here.

############ APPSFLYER ############
-keep class com.appsflyer.** { *; }
-keep class com.appsflyer.internal.** { *; }
-dontwarn com.appsflyer.**

-keep class com.android.installreferrer.** { *; }
-dontwarn com.android.installreferrer.**

############ FIREBASE ############
-keep class com.google.firebase.installations.** { *; }
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-keepclassmembers class com.google.firebase.iid.** { *; }

-keep class com.google.android.gms.ads.identifier.** { *; }
-dontwarn com.google.android.gms.ads.identifier.**

-dontwarn com.google.firebase.analytics.**
-dontwarn com.google.firebase.messaging.**
-dontwarn com.google.firebase.iid.**
-dontwarn com.google.firebase.installations.**

############ KOTLIN ############
-keep class kotlin.jvm.internal.** { *; }

############ KOIN ############
# Сохранить Koin Core
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Сохранить твои классы, которые создаются через Koin (важно!)
-keep class * implements org.koin.core.component.KoinComponent { *; }

-keep class org.koin.android.** { *; }
-dontwarn org.koin.android.**

############ ROOM ############
# Хранить аннотации
-keepattributes *Annotation*

# Сохранить DAO и сущности
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Сохранить модели с @Entity, чтобы поля не обфусцировались
-keep @androidx.room.Entity class * { *; }

# Сохранить абстрактный класс Database
-keep class * extends androidx.room.RoomDatabase { *; }

-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

############ GSON ############
# Gson uses generic type information stored in a class file when working with fields.
# Proguard removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic signature of TypeToken (class uses TypeToken to get generic info)
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Application classes that will be serialized/deserialized over Gson
-keep class com.buildsof.budsde.data.** { *; }
-keep class com.buildsof.budsde.data.room.** { *; }

# Keep WorkConfig sealed class and all subclasses
-keep class com.buildsof.budsde.data.WorkConfig { *; }
-keep class com.buildsof.budsde.data.WorkConfig$* { *; }

# Keep custom TypeAdapter
-keep class com.buildsof.budsde.data.room.WorkConfigTypeAdapter { *; }

# Keep all model classes fields
-keepclassmembers class com.buildsof.budsde.data.** { *; }
