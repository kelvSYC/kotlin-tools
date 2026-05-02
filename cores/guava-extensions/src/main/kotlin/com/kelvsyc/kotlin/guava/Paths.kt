package com.kelvsyc.kotlin.guava

import com.google.common.annotations.Beta
import com.google.common.io.ByteSink
import com.google.common.io.ByteSource
import com.google.common.io.CharSink
import com.google.common.io.CharSource
import com.google.common.io.MoreFiles
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path

@Beta
fun Path.asByteSource(vararg options: OpenOption): ByteSource = MoreFiles.asByteSource(this, *options)

@Beta
fun Path.asCharSource(charset: Charset, vararg options: OpenOption): CharSource = MoreFiles.asCharSource(this, charset, *options)

@Beta
fun Path.asByteSink(vararg options: OpenOption): ByteSink = MoreFiles.asByteSink(this, *options)

@Beta
fun Path.asCharSink(charset: Charset, vararg options: OpenOption): CharSink = MoreFiles.asCharSink(this, charset, *options)
