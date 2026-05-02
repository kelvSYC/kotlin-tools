package com.kelvsyc.kotlin.guava

import com.google.common.annotations.Beta
import com.google.common.io.ByteSink
import com.google.common.io.ByteSource
import com.google.common.io.CharSink
import com.google.common.io.CharSource
import com.google.common.io.FileWriteMode
import com.google.common.io.Files as GuavaFiles
import java.io.File
import java.nio.charset.Charset

@Beta
fun File.asByteSource(): ByteSource = GuavaFiles.asByteSource(this)

@Beta
fun File.asCharSource(charset: Charset): CharSource = GuavaFiles.asCharSource(this, charset)

@Beta
fun File.asByteSink(vararg modes: FileWriteMode): ByteSink = GuavaFiles.asByteSink(this, *modes)

@Beta
fun File.asCharSink(charset: Charset, vararg modes: FileWriteMode): CharSink = GuavaFiles.asCharSink(this, charset, *modes)
