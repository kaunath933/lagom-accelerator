Common-lagom
====
This is a Lagom framework module which can be implemented by another module. Implementing common-lagom will reduce a lot of ectra code.
  
## How to use
To use it, clone the repo from github
`https://github.com/kaunath933/common-lagom.git`  
  
You will see an example of **customer-lagom-api** and **customer-lagom-impl** that are using functionalities of common-lagom.
  
## How to define your own services  
in `build.sbt`   

```bash 
   lazy val <your-service-api-name> = (project in file("modules/<your-serive-api-name>"))
  .settings(name := <name of your project>) 
  .settings(commonLagomAPISettings: _*)
  ```
```bash
lazy val <your-serive-name> = (project in file("modules/<your-service-name>"))
  .enablePlugins(LagomScala)
  .settings(name := <name of your project>)
  .settings(commonLagomImplSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`common-lagom`)
  .dependsOn(<your-service-api-name>)
```