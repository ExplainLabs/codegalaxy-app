package io.codegalaxy.domain

import scommons.websql.Database
import scommons.websql.io.SqliteContext

class CodeGalaxyDBContext(db: Database) extends SqliteContext(db) {

  //TODO: move to scommons-websql-encoding module
  implicit def seqEncoder[T](implicit e: Encoder[T]): Encoder[Seq[T]] =
    WebSqlEncoder { (_: Index, seq: Seq[T], row: PrepareRow) =>
      val values = seq.map(v => e(-1, v, Nil).head)
      row :++ values
    }
}
