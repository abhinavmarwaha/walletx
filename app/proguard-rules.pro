# Open Source so what the hell
-dontobfuscate
-keepattributes RuntimeVisibleAnnotations
-keep class kotlin.Metadata { *; }
-keep class com.abhinavmarwaha.** { *; }

# Sql Cypher
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

# Rome
-keep class com.rometools.** { *; }

# DataStore
-keep class androidx.datastore.*.** {*;}
-keep class io.github.osipxd.datastore.encrypted.** { *; }