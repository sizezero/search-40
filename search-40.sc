#!/usr/bin/env amm

import ammonite.ops._

val qmkDir: os.Path = os.home / "qmk_firmware_kleemann"
val infoDirs = (ls.rec! (qmkDir / "keyboards")).filter(d => d.isDir && (d / "info.json").toIO.isFile)

val fin = infoDirs.map{ d => (d, d / "info.json") }.flatMap{
// see if the info file in the keyboard dir is a candidate for a 40% keyboard
case (d,f) => {
  try {
    val j = ujson.read(os.read(f))
    val fortyLayouts =
      if (j.obj.contains("layouts")) {
        j("layouts").obj.values.flatMap(lay => {
          if (lay.obj.contains("layout")) {
            val s = lay("layout").arr.size
            if (s>45 && s<55) List(lay) else Nil
          } else
            Nil
        })
      } else Nil
    // if any of the layouts match, show the directory
    if (fortyLayouts.isEmpty) Nil else List(d)
  } catch {
    // if we can't even read the json, then the project is probably dead
    case e: Exception => {
      //println("bad json file: "+f)
      Nil
    }
  }
}}

for (d <- fin) {
  println(d)
}
