package io.codegalaxy.domain

import io.getquill.SnakeCase
import scommons.websql.Database
import scommons.websql.quill.SqliteContext

class CodeGalaxyDBContext(db: Database) extends SqliteContext(SnakeCase, db)
