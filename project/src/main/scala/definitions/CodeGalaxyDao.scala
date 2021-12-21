package definitions

import common.Libs
import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._

import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin

object CodeGalaxyDao extends CodeGalaxyModule {

  override val id = "codegalaxy-dao"

  override val base: File = file("dao")

  override def definition: Project = super.definition
    .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
    .settings(
      description := "DAO module for CodeGalaxy app"
    )

  override def internalDependencies: Seq[ClasspathDep[ProjectReference]] = Nil

  override def superRepoProjectsDependencies: Seq[(String, String, Option[String])] = Seq(
    ("scommons-websql", "scommons-websql-migrations", None),
    ("scommons-websql", "scommons-websql-io", None)
  )
  
  override def runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.scommonsWebSqlMigrations.value,
    Libs.scommonsWebSqlIO.value
  ))
  
  override def testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Nil)
}
