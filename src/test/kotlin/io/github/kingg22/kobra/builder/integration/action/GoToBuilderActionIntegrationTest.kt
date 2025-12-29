package io.github.kingg22.kobra.builder.integration.action

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.requireIs
import io.github.kingg22.kobra.builder.action.handler.GoToBuilderActionHandler
import io.github.kingg22.kobra.builder.finder.BuilderFinder
import io.github.kingg22.kobra.builder.psi.PsiHelper

class GoToBuilderActionIntegrationTest : BasePlatformTestCase() {

    /**
     * Test: Navegar desde una clase hacia su builder existente
     * Escenario: Usuario tiene Person.java abierto y existe PersonBuilder.java
     * Resultado esperado: El editor navega a PersonBuilder.java
     */
    fun testNavigateFromClassToExistingBuilder() {
        // ARRANGE: Crear la clase Person
        val personClass = myFixture.addFileToProject(
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
                public void setName(String name) { this.name = name; }
                public int getAge() { return age; }
                public void setAge(int age) { this.age = age; }
            }
            """.trimIndent(),
        )

        // ARRANGE: Crear el builder existente
        myFixture.addFileToProject(
            "PersonBuilder.java",
            """
            package com.example;

            public final class PersonBuilder {
                private String name;
                private int age;

                private PersonBuilder() {}

                public static PersonBuilder aPerson() {
                    return new PersonBuilder();
                }

                public PersonBuilder withName(String name) {
                    this.name = name;
                    return this;
                }

                public PersonBuilder withAge(int age) {
                    this.age = age;
                    return this;
                }

                public Person build() {
                    return new Person(name, age);
                }
            }
            """.trimIndent(),
        )

        // ARRANGE: Abrir Person.java en el editor
        myFixture.openFileInEditor(personClass.containingFile.virtualFile)

        // ACT: Ejecutar la acción de navegación
        val handler = GoToBuilderActionHandler()
        handler.execute(myFixture.editor, null, null)

        // ASSERT: Verificar que el archivo actual es PersonBuilder.java
        val currentFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        assertNotNull("Debe haber un archivo abierto", currentFile)
        assertEquals("PersonBuilder.java", currentFile!!.name)

        // ASSERT: Verificar que el contenido es el builder
        myFixture.configureFromExistingVirtualFile(currentFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val currentClass = psiFile.classes.firstOrNull()
        assertNotNull("Debe haber una clase en el archivo", currentClass)
        assertEquals("PersonBuilder", currentClass!!.name)
    }

    /**
     * Test: Navegar desde un builder hacia su clase original
     * Escenario: Usuario tiene PersonBuilder.java abierto y existe Person.java
     * Resultado esperado: El editor navega a Person.java
     */
    fun testNavigateFromBuilderToClass() {
        // ARRANGE: Crear la clase Person
        myFixture.addFileToProject(
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
            }
            """.trimIndent(),
        )

        // ARRANGE: Crear el builder
        val personBuilder = myFixture.addFileToProject(
            "PersonBuilder.java",
            """
            package com.example;

            public final class PersonBuilder {
                private String name;
                private int age;

                public Person build() {
                    return new Person(name, age);
                }
            }
            """.trimIndent(),
        )

        // ARRANGE: Abrir PersonBuilder.java en el editor
        myFixture.openFileInEditor(personBuilder.containingFile.virtualFile)

        // ACT: Ejecutar la acción de navegación
        val handler = GoToBuilderActionHandler()
        handler.execute(myFixture.editor, null, null)

        // ASSERT: Verificar que el archivo actual es Person.java
        val currentFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        assertNotNull("Debe haber un archivo abierto", currentFile)
        assertEquals("Person.java", currentFile!!.name)

        // ASSERT: Verificar que el contenido es la clase original
        myFixture.configureFromExistingVirtualFile(currentFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val currentClass = psiFile.classes.firstOrNull()
        assertNotNull("Debe haber una clase en el archivo", currentClass)
        assertEquals("Person", currentClass!!.name)
    }

    /**
     * Test: Generar builder cuando no existe
     * Escenario: Usuario tiene Customer.java abierto pero NO existe CustomerBuilder.java
     * Resultado esperado: Se debe mostrar el popup para crear el builder
     *
     * NOTA: Este test simula la acción pero NO ejecuta el diálogo UI completo.
     * Solo verifica que se detecta correctamente la ausencia del builder.
     */
    fun testGenerateBuilderWhenNotExists() {
        // ARRANGE: Crear solo la clase Customer (sin builder)
        val customerClass = myFixture.addFileToProject(
            "Customer.java",
            """
            package com.example;

            public class Customer {
                private String email;
                private String phone;

                public Customer(String email, String phone) {
                    this.email = email;
                    this.phone = phone;
                }

                public String getEmail() { return email; }
                public void setEmail(String email) { this.email = email; }
                public String getPhone() { return phone; }
                public void setPhone(String phone) { this.phone = phone; }
            }
            """.trimIndent(),
        )

        // ARRANGE: Abrir Customer.java en el editor
        myFixture.openFileInEditor(customerClass.containingFile.virtualFile)

        // ACT: Verificar que NO existe un builder
        val psiClassFromEditor = PsiHelper.getPsiClassFromEditor(myFixture.editor, project)
        assertNotNull("Debe haber una clase en el editor", psiClassFromEditor)

        val existingBuilder = BuilderFinder.findBuilderForClass(psiClassFromEditor!!)

        // ASSERT: Verificar que no existe builder
        assertNull("No debe existir CustomerBuilder", existingBuilder)

        // ASSERT: Verificar que la clase actual NO es un builder
        val isBuilder = existingBuilder != null || psiClassFromEditor.name?.endsWith("Builder") == true
        assertFalse("La clase actual no debe ser un builder", isBuilder)
    }

    /**
     * Test: Verificar el comportamiento del shortcut Shift+Alt+B
     * Escenario: Usuario presiona Shift+Alt+B sobre una clase que tiene builder
     * Resultado esperado: Navega al builder
     *
     * NOTA: Este test verifica el mismo comportamiento que testNavigateFromClassToExistingBuilder
     * pero enfatiza que es activado por el shortcut
     */
    fun testShortcutShiftAltB() {
        // ARRANGE: Crear clase y builder
        val orderClass = myFixture.addFileToProject(
            "Order.java",
            """
            package com.example;

            public class Order {
                private String orderId;
                private double total;

                public Order(String orderId, double total) {
                    this.orderId = orderId;
                    this.total = total;
                }

                public String getOrderId() { return orderId; }
                public double getTotal() { return total; }
            }
            """.trimIndent(),
        )

        myFixture.addFileToProject(
            "OrderBuilder.java",
            """
            package com.example;

            public final class OrderBuilder {
                private String orderId;
                private double total;

                public Order build() {
                    return new Order(orderId, total);
                }
            }
            """.trimIndent(),
        )

        // ARRANGE: Abrir Order.java
        myFixture.openFileInEditor(orderClass.containingFile.virtualFile)

        // ACT: Simular la acción del shortcut (invoca el mismo handler)
        val handler = GoToBuilderActionHandler()
        handler.execute(myFixture.editor, null, null)

        // ASSERT: Verificar navegación al builder
        val currentFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        assertNotNull("Debe haber un archivo abierto", currentFile)
        assertEquals("OrderBuilder.java", currentFile!!.name)
    }

    /**
     * Test adicional: Navegar entre inner builder y clase
     * Escenario: Builder está definido como clase interna estática
     * Resultado esperado: Debe navegar correctamente
     */
    fun testNavigateWithInnerBuilder() {
        // ARRANGE: Crear clase con inner builder
        myFixture.configureByText(
            "Product.java",
            """
            package com.example;

            public class Product {
                private String name;
                private double price;

                private Product(String name, double price) {
                    this.name = name;
                    this.price = price;
                }

                public static final class Builder {
                    private String name;
                    private double price;

                    public Builder withName(String name) {
                        this.name = name;
                        return this;
                    }

                    public Builder withPrice(double price) {
                        this.price = price;
                        return this;
                    }

                    public Product build() {
                        return new Product(name, price);
                    }
                }
            }
            """.trimIndent(),
        )

        // ACT: Obtener las clases del archivo
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val productClass = psiFile.classes.first { it.name == "Product" }
        val builderClass = productClass.innerClasses.first { it.name == "Builder" }

        // ASSERT: Verificar que el builder es inner y static
        assertNotNull("Debe existir el inner builder", builderClass)
        assertTrue("El builder debe ser static", builderClass.hasModifierProperty("static"))

        // ASSERT: Verificar que BuilderFinder puede encontrar el inner builder
        val foundBuilder = BuilderFinder.findBuilderForClass(productClass)
        assertNotNull("BuilderFinder debe encontrar el inner builder", foundBuilder)
        assertEquals("Builder", foundBuilder?.name)
    }
}
