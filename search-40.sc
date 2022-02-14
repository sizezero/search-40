#!/usr/bin/env amm

import ammonite.ops._

// the format of this json file contains a layout array. This array
// determines the number of keys in the keyboard. Around 48 keys is the
// size of a 40% keyboard
def keyboardDirIsFortyPercent(jsonFile: os.Path): Boolean = {
  try {
    val j = ujson.read(os.read(jsonFile))
    // An info.json can have many layouts. Keep the ones that match our criteria.
    val fortyLayouts =
      if (j.obj.contains("layouts")) {
        j("layouts").obj.values.flatMap {
          lay => {
            if (lay.obj.contains("layout")) {
              val nKeys = lay("layout").arr.size
              if (nKeys>45 && nKeys<55) List(lay) else Nil
            } else Nil
          }
        }
      } else Nil
    // if any of the layouts match, show the directory
    !fortyLayouts.isEmpty
  } catch {
    // if we can't even read the json, then the project is probably
    // dead and we consider it "not a 40% layout
    case e: Exception => false
  }
}

// all keyboard (dir, infoFile) pairs that contain 45 to 55 keys
val good =
(ls.rec! os.home / "qmk_firmware_kleemann" / "keyboards")
.filter{ _.isDir }
.map{ d => (d, d / "info.json") }
.filter{ case (d,f) => f.toIO.isFile }
.flatMap {
  // see if the info file in the keyboard dir is a candidate for a 40% keyboard
  case (d,f) =>
    if (keyboardDirIsFortyPercent(f)) List(d)
    else Nil
}

for (d <- good) {
  println(d)
}
