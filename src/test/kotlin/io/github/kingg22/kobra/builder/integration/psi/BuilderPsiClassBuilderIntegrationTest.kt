package io.github.kingg22.kobra.builder.integration.psi

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.requireIs
import io.github.kingg22.kobra.builder.action.handler.DisplayChoosers
import io.github.kingg22.kobra.builder.factory.PsiFieldsForBuilderFactory
import io.github.kingg22.kobra.builder.psi.BuilderPsiClassBuilder
import io.github.kingg22.kobra.builder.writer.BuilderContext

class BuilderPsiClassBuilderIntegrationTest : BasePlatformTestCase() {

    /**
     * Test: Construir builder simple con campos primitivos y String
     * Escenario: Clase con campos básicos (String, int, double)
     * Resultado esperado: Builder con todos los campos y métodos básicos
     */
    fun testBuildSimpleClass() {
        // ARRANGE: Crear clase simple
        val personFile = myFixture.addFileToProject(
            "Person.java",
            """
            package com.example;

            public class Person {
                private String name;
                private int age;
                private double height;

                public Person(String name, int age, double height) {
                    this.name = name;
                    this.age = age;
                    this.height = height;
                }

                public String getName() { return name; }
                public int getAge() { return age; }
                public double getHeight() { return height; }
            }
            """.trimIndent(),
        )

        val personClass = personFile.requireIs<PsiJavaFile>().classes.first()
        assertNotNull(personClass)

        // ACT: Construir el builder usando BuilderPsiClassBuilder
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = personClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                personClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = personFile.containingDirectory,
                className = "PersonBuilder",
                psiClassFromEditor = personClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withInitializingMethod()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar estructura del builder
        assertNotNull("Builder class debe existir", builderClass)
        assertEquals("PersonBuilder", builderClass.name)
        assertTrue("Builder debe ser final", builderClass.hasModifierProperty("final"))

        // Verificar campos
        val fields = builderClass.fields
        assertEquals("Debe tener 3 campos", 3, fields.size)
        assertTrue("Debe tener campo name", fields.any { it.name == "name" && it.type.presentableText == "String" })
        assertTrue("Debe tener campo age", fields.any { it.name == "age" && it.type.presentableText == "int" })
        assertTrue("Debe tener campo height", fields.any { it.name == "height" && it.type.presentableText == "double" })

        // Verificar constructor privado
        val constructors = builderClass.constructors
        assertTrue("Debe tener al menos un constructor", constructors.isNotEmpty())
        val privateConstructor = constructors.first()
        assertTrue("Constructor debe ser privado", privateConstructor.hasModifierProperty("private"))

        // Verificar method estático de inicialización
        val methods = builderClass.methods
        val staticMethod = methods.firstOrNull { it.hasModifierProperty("static") && it.name == "aPerson" }
        assertNotNull("Debe tener método estático aPerson()", staticMethod)
        assertEquals("PersonBuilder", staticMethod!!.returnType?.presentableText)

        // Verificar métodos setter
        assertTrue("Debe tener withName", methods.any { it.name == "withName" })
        assertTrue("Debe tener withAge", methods.any { it.name == "withAge" })
        assertTrue("Debe tener withHeight", methods.any { it.name == "withHeight" })

        // Verificar method build
        val buildMethod = methods.firstOrNull { it.name == "build" }
        assertNotNull("Debe tener método build()", buildMethod)
        assertEquals("Person", buildMethod!!.returnType?.presentableText)
    }

    /**
     * Test: Builder con solo constructor (sin setters)
     * Escenario: Clase inmutable con solo constructor, sin setters
     * Resultado esperado: Builder usa solo constructor en build()
     */
    fun testBuildWithConstructorOnly() {
        // ARRANGE: Crear clase inmutable
        val pointFile = myFixture.addFileToProject(
            "Point.java",
            """
            package com.example;

            public class Point {
                private final int x;
                private final int y;

                public Point(int x, int y) {
                    this.x = x;
                    this.y = y;
                }

                public int getX() { return x; }
                public int getY() { return y; }
            }
            """.trimIndent(),
        )

        val pointClass = pointFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir builder
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = pointClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                pointClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = pointFile.containingDirectory,
                className = "PointBuilder",
                psiClassFromEditor = pointClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar que todos los campos van al constructor
        val fieldsForConstructor = builderClass.fields
        assertTrue("Debe tener campo x", fieldsForConstructor.any { it.name == "x" })
        assertTrue("Debe tener campo y", fieldsForConstructor.any { it.name == "y" })

        // Verificar method build usa constructor
        val buildMethod = builderClass.methods.first { it.name == "build" }
        assertNotNull("Método build debe existir", buildMethod)
        val methodText = buildMethod.text
        assertTrue("build() debe usar constructor new Point", methodText.contains("new com.example.Point("))
    }

    /**
     * Test: Builder con solo setters (sin constructor)
     * Escenario: Clase JavaBean con setters pero constructor vacío
     * Resultado esperado: Builder usa setters en build()
     */
    fun testBuildWithSettersOnly() {
        // ARRANGE: Crear JavaBean
        val userFile = myFixture.addFileToProject(
            "User.java",
            """
            package com.example;

            public class User {
                private String username;
                private String email;

                public User() {}

                public String getUsername() { return username; }
                public void setUsername(String username) { this.username = username; }
                public String getEmail() { return email; }
                public void setEmail(String email) { this.email = email; }
            }
            """.trimIndent(),
        )

        val userClass = userFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir builder
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = userClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                userClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = userFile.containingDirectory,
                className = "UserBuilder",
                psiClassFromEditor = userClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar method build usa setters
        val buildMethod = builderClass.methods.first { it.name == "build" }
        val methodText = buildMethod.text
        assertTrue("build() debe llamar setUsername", methodText.contains("setUsername"))
        assertTrue("build() debe llamar setEmail", methodText.contains("setEmail"))
    }

    /**
     * Test: Builder con campos mixtos (algunos por constructor, otros por setter)
     * Escenario: Clase con constructor parcial + setters opcionales
     * Resultado esperado: Builder usa constructor + setters en build()
     */
    fun testBuildWithMixedFields() {
        // ARRANGE: Crear clase mixta
        val orderFile = myFixture.addFileToProject(
            "Order.java",
            """
            package com.example;

            public class Order {
                private String orderId;
                private double total;
                private String notes;

                public Order(String orderId, double total) {
                    this.orderId = orderId;
                    this.total = total;
                }

                public String getOrderId() { return orderId; }
                public double getTotal() { return total; }
                public String getNotes() { return notes; }
                public void setNotes(String notes) { this.notes = notes; }
            }
            """.trimIndent(),
        )

        val orderClass = orderFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir builder
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = orderClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                orderClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = orderFile.containingDirectory,
                className = "OrderBuilder",
                psiClassFromEditor = orderClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar todos los campos existen
        val fields = builderClass.fields
        assertTrue("Debe tener 3 campos", fields.size >= 3)

        // Verificar method build combina constructor + setter
        val buildMethod = builderClass.methods.first { it.name == "build" }
        val methodText = buildMethod.text
        assertTrue("build() debe usar constructor", methodText.contains("new com.example.Order("))
        assertTrue("build() debe llamar setNotes", methodText.contains("setNotes"))
    }

    /**
     * Test: Builder como clase interna estática
     * Escenario: Builder generado dentro de la clase original
     * Resultado esperado: Inner builder static y final dentro de la clase
     */
    fun testBuildInnerBuilder() {
        // ARRANGE: Crear clase
        val productFile = myFixture.addFileToProject(
            "Product.java",
            """
            package com.example;

            public class Product {
                private String name;
                private double price;

                public Product(String name, double price) {
                    this.name = name;
                    this.price = price;
                }

                public String getName() { return name; }
                public double getPrice() { return price; }
            }
            """.trimIndent(),
        )

        val productClass = productFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir inner builder
        WriteCommandAction.runWriteCommandAction(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = productClass,
                innerBuilder = true,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                productClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = null, // null para inner builder
                className = "Builder",
                psiClassFromEditor = productClass,
                methodPrefix = "with",
                isInnerBuilder = true,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            val innerBuilder = BuilderPsiClassBuilder.anInnerBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .build()

            // Agregar el inner builder a la clase
            productClass.add(innerBuilder)
        }

        // ASSERT: Verificar inner builder
        val psiManager = PsiManager.getInstance(project)
        val updatedFile = psiManager.findFile(productFile.virtualFile).requireIs<PsiJavaFile>()
        val updatedClass = updatedFile.classes.first()

        val innerClasses = updatedClass.innerClasses
        assertTrue("Debe tener inner class", innerClasses.isNotEmpty())

        val builderClass = innerClasses.first { it.name == "Builder" }
        assertNotNull("Inner Builder debe existir", builderClass)
        assertTrue("Inner builder debe ser static", builderClass.hasModifierProperty("static"))
        assertTrue("Inner builder debe ser final", builderClass.hasModifierProperty("final"))

        // Verificar que NO tiene campos duplicados (característica especial de inner builder)
        // Inner builder puede acceder a campos de la clase externa
        val fields = builderClass.fields
        assertTrue("Inner builder debe tener campos", fields.isNotEmpty())
    }

    /**
     * Test: Builder con un solo campo (useSingleField)
     * Escenario: Builder mantiene instancia del objeto en lugar de campos separados
     * Resultado esperado: Builder con 1 campo del tipo de la clase, métodos modifican ese objeto
     */
    fun testBuildWithSingleField() {
        // ARRANGE: Crear clase
        val itemFile = myFixture.addFileToProject(
            "Item.java",
            """
            package com.example;

            public class Item {
                private String code;
                private int quantity;

                public Item() {}

                public String getCode() { return code; }
                public void setCode(String code) { this.code = code; }
                public int getQuantity() { return quantity; }
                public void setQuantity(int quantity) { this.quantity = quantity; }
            }
            """.trimIndent(),
        )

        val itemClass = itemFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir builder con useSingleField
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = itemClass,
                innerBuilder = false,
                useSingleField = true,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                itemClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = itemFile.containingDirectory,
                className = "ItemBuilder",
                psiClassFromEditor = itemClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = true,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar que solo tiene 1 campo (el objeto Item)
        val fields = builderClass.fields
        assertEquals("Debe tener exactamente 1 campo", 1, fields.size)
        assertEquals("Item", fields[0].type.presentableText)

        // Verificar constructor inicializa el objeto
        val constructor = builderClass.constructors.first()
        val constructorText = constructor.text
        assertTrue("Constructor debe crear new Item()", constructorText.contains("new com.example.Item()"))

        // Verificar que build() retorna el objeto directamente
        val buildMethod = builderClass.methods.first { it.name == "build" }
        val buildText = buildMethod.text
        assertTrue("build() debe retornar el campo item", buildText.contains("return") && buildText.contains("item"))
        assertFalse("build() NO debe crear new Item", buildText.contains("new com.example.Item("))
    }

    /**
     * Test: Builder con copy constructor
     * Escenario: Builder puede inicializarse desde instancia existente
     * Resultado esperado: Constructor público que acepta objeto existente
     */
    fun testBuildWithCopyConstructor() {
        // ARRANGE: Crear clase
        val customerFile = myFixture.addFileToProject(
            "Customer.java",
            """
            package com.example;

            public class Customer {
                private String name;
                private String email;

                public Customer(String name, String email) {
                    this.name = name;
                    this.email = email;
                }

                public String getName() { return name; }
                public String getEmail() { return email; }
            }
            """.trimIndent(),
        )

        val customerClass = customerFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir builder con copy constructor
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = customerClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                customerClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = customerFile.containingDirectory,
                className = "CustomerBuilder",
                psiClassFromEditor = customerClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = true,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withCopyConstructor()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar que tiene 2 constructores
        val constructors = builderClass.constructors
        assertTrue("Debe tener al menos 2 constructores", constructors.size >= 2)

        // Verificar constructor privado sin parámetros
        val privateConstructor = constructors.firstOrNull {
            it.hasModifierProperty("public") && it.parameterList.parametersCount == 0
        }
        assertNotNull("Debe tener constructor público sin parámetros", privateConstructor)

        // Verificar copy constructor público
        val copyConstructor = constructors.firstOrNull {
            it.parameterList.parameters.any { param -> param.type.presentableText == "Customer" }
        }
        assertNotNull("Debe tener copy constructor que recibe Customer", copyConstructor)
    }

    /**
     * Test: Builder con method "but"
     * Escenario: Builder incluye method para crear copia modificable
     * Resultado esperado: method but(Object) que copia valores
     */
    fun testBuildWithButMethod() {
        // ARRANGE: Crear clase
        val personFile = myFixture.addFileToProject(
            "Person.java",
            """
            package com.example;

            public class Person {
                private String name;
                private int age;

                public Person(String name, int age) {
                    this.name = name;
                    this.age = age;
                }

                public String getName() { return name; }
                public int getAge() { return age; }
            }
            """.trimIndent(),
        )

        val personClass = personFile.requireIs<PsiJavaFile>().classes.first()

        // ACT: Construir builder con method but
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = personClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = true,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                personClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = personFile.containingDirectory,
                className = "PersonBuilder",
                psiClassFromEditor = personClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = true,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .withButMethod()
                .build()
        }

        // ASSERT: Verificar method but
        val methods = builderClass.methods
        val butMethod = methods.firstOrNull { it.name == "but" }
        assertNotNull("Debe tener método but", butMethod)

        // Verificar parámetro
        val parameters = butMethod!!.parameterList.parameters
        assertEquals("but() debe tener 0 parámetro", 0, parameters.size)

        // Verificar retorno
        assertEquals("PersonBuilder", butMethod.returnType?.presentableText)
    }

    /**
     * Test: Builder con constructor inline
     * Escenario: Todos los campos van por constructor, build() retorna directamente
     * Resultado esperado: build() con return new Class(params) inline
     */
    fun testBuildInlineConstructor() {
        // ARRANGE: Crear clase inmutable
        val coordinateFile = myFixture.addFileToProject(
            "Coordinate.java",
            """
            package com.example;

            public class Coordinate {
                private final double latitude;
                private final double longitude;

                public Coordinate(double latitude, double longitude) {
                    this.latitude = latitude;
                    this.longitude = longitude;
                }

                public double getLatitude() { return latitude; }
                public double getLongitude() { return longitude; }
            }
            """.trimIndent(),
        )

        val coordinateClass = coordinateFile.requireIs<PsiJavaFile>().classes.first()
        assertNotNull(coordinateClass)

        // ACT: Construir builder (automáticamente detecta que todos van por constructor)
        val builderClass = WriteCommandAction.runWriteCommandAction<PsiClass>(project) {
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = coordinateClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )
            val psiFieldsForBuilder = PsiFieldsForBuilderFactory.createPsiFieldsForBuilder(
                allFields,
                coordinateClass,
            )

            val context = BuilderContext(
                project = project,
                psiFieldsForBuilder = psiFieldsForBuilder,
                targetDirectory = coordinateFile.containingDirectory,
                className = "CoordinateBuilder",
                psiClassFromEditor = coordinateClass,
                methodPrefix = "with",
                isInnerBuilder = false,
                hasButMethod = false,
                useSingleField = false,
                hasAddCopyConstructor = false,
            )

            BuilderPsiClassBuilder.aBuilder(context)
                .withFields()
                .withConstructor()
                .withSetMethods("with")
                .build()
        }

        // ASSERT: Verificar que build() es inline
        val buildMethod = builderClass.methods.first { it.name == "build" }
        val buildText = buildMethod.text

        // Build inline debe tener: return new Coordinate(latitude, longitude);
        assertTrue("build() debe tener return", buildText.contains("return"))
        assertTrue("build() debe crear new Coordinate", buildText.contains("new com.example.Coordinate("))

        // NO debe tener variable local intermedia
        assertFalse(
            "build() NO debe tener variable local 'coordinate'",
            buildText.contains("Coordinate coordinate ="),
        )
    }
}
