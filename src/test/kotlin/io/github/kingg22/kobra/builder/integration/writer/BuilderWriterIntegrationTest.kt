package io.github.kingg22.kobra.builder.integration.writer

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.requireIs
import io.github.kingg22.kobra.builder.action.handler.DisplayChoosers
import io.github.kingg22.kobra.builder.factory.PsiFieldsForBuilderFactory
import io.github.kingg22.kobra.builder.finder.BuilderFinder
import io.github.kingg22.kobra.builder.writer.BuilderContext
import io.github.kingg22.kobra.builder.writer.BuilderWriter

class BuilderWriterIntegrationTest : BasePlatformTestCase() {

    /**
     * Test: Generar builder desde el menú Code | Generate cuando NO existe
     * Escenario: Usuario invoca "Generate Builder" sobre Customer.java (sin builder)
     * Resultado esperado: Se crea CustomerBuilder.java con todos los campos
     */
    fun testGenerateBuilderFromCodeMenu() {
        // ARRANGE: Crear clase sin builder
        val customerFile = myFixture.addFileToProject(
            "Customer.java",
            """
            package com.example;

            public class Customer {
                private String email;
                private String phone;
                private String address;

                public Customer(String email, String phone, String address) {
                    this.email = email;
                    this.phone = phone;
                    this.address = address;
                }

                public String getEmail() { return email; }
                public void setEmail(String email) { this.email = email; }
                public String getPhone() { return phone; }
                public void setPhone(String phone) { this.phone = phone; }
                public String getAddress() { return address; }
                public void setAddress(String address) { this.address = address; }
            }
            """.trimIndent(),
        )

        myFixture.openFileInEditor(customerFile.virtualFile)

        // ACT: Simular la generación del builder programáticamente
        // En lugar de ejecutar el handler (que muestra UI), ejecutamos directamente el BuilderWriter
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val customerClass = psiFile.classes.first()

        val builderCreated = WriteCommandAction.runWriteCommandAction<Boolean>(project) {
            // Obtener todos los campos de la clase
            val allFields = DisplayChoosers.getFieldsToIncludeInBuilder(
                clazz = customerClass,
                innerBuilder = false,
                useSingleField = false,
                hasButMethod = false,
            )

            // Crear el contexto para el builder
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
                hasAddCopyConstructor = false,
            )

            // Generar el builder
            BuilderWriter.writeBuilder(context, null)
            true
        }

        // ASSERT: Verificar que se creó el builder
        assertTrue("El builder debe haberse creado", builderCreated)

        val builderFile = myFixture.findFileInTempDir("CustomerBuilder.java")
        assertNotNull("Debe existir CustomerBuilder.java", builderFile)

        // ASSERT: Verificar el contenido del builder
        val builderPsiFile = PsiManager.getInstance(project).findFile(builderFile).requireIs<PsiJavaFile>()
        val builderClass = builderPsiFile.classes.first()

        assertEquals("CustomerBuilder", builderClass.name)
        assertTrue("El builder debe ser final", builderClass.hasModifierProperty("final"))

        // Verificar que tiene los campos
        val fields = builderClass.fields
        assertTrue("Debe tener al menos 3 campos", fields.size >= 3)
        assertTrue("Debe tener campo email", fields.any { it.name == "email" })
        assertTrue("Debe tener campo phone", fields.any { it.name == "phone" })
        assertTrue("Debe tener campo address", fields.any { it.name == "address" })

        // Verificar que tiene métodos setter (withXxx)
        val methods = builderClass.methods
        assertTrue("Debe tener método withEmail", methods.any { it.name == "withEmail" })
        assertTrue("Debe tener método withPhone", methods.any { it.name == "withPhone" })
        assertTrue("Debe tener método withAddress", methods.any { it.name == "withAddress" })
        assertTrue("Debe tener método build", methods.any { it.name == "build" })

        // Verificar method estático de inicialización (aCustomer)
        assertTrue(
            "Debe tener método estático aCustomer",
            methods.any { it.name == "aCustomer" && it.hasModifierProperty("static") },
        )
    }

    /**
     * Test: Regenerar builder existente
     * Escenario: Usuario modifica Order.java agregando campo, luego regenera OrderBuilder
     * Resultado esperado: OrderBuilder se actualiza con el nuevo campo
     */
    fun testRegenerateExistingBuilder() {
        // ARRANGE: Crear clase inicial
        val orderFile = myFixture.addFileToProject(
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

        // ARRANGE: Crear builder inicial
        myFixture.addFileToProject(
            "OrderBuilder.java",
            """
            package com.example;

            public final class OrderBuilder {
                private String orderId;
                private double total;

                private OrderBuilder() {}

                public static OrderBuilder anOrder() {
                    return new OrderBuilder();
                }

                public OrderBuilder withOrderId(String orderId) {
                    this.orderId = orderId;
                    return this;
                }

                public OrderBuilder withTotal(double total) {
                    this.total = total;
                    return this;
                }

                public Order build() {
                    return new Order(orderId, total);
                }
            }
            """.trimIndent(),
        )

        // ACT: Modificar Order.java para agregar nuevo campo
        WriteCommandAction.runWriteCommandAction(project) {
            val document = myFixture.getDocument(orderFile)
            document.setText(
                """
            package com.example;

            public class Order {
                private String orderId;
                private double total;
                private String customerName;

                public Order(String orderId, double total, String customerName) {
                    this.orderId = orderId;
                    this.total = total;
                    this.customerName = customerName;
                }

                public String getOrderId() { return orderId; }
                public double getTotal() { return total; }
                public String getCustomerName() { return customerName; }
                public void setCustomerName(String customerName) { this.customerName = customerName; }
            }
                """.trimIndent(),
            )
        }

        // ACT: Regenerar el builder
        myFixture.openFileInEditor(orderFile.virtualFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val orderClass = psiFile.classes.first()

        // Obtener el builder existente
        val existingBuilder = BuilderFinder.findBuilderForClass(orderClass)
        assertNotNull("Debe existir el builder antes de regenerar", existingBuilder)

        WriteCommandAction.runWriteCommandAction(project) {
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

            // Regenerar el builder (pasando el existente para sobrescribirlo)
            BuilderWriter.writeBuilder(context, existingBuilder)
        }

        // ASSERT: Verificar que el builder se actualizó
        val regeneratedBuilderFile = myFixture.findFileInTempDir("OrderBuilder.java")
        assertNotNull("El builder debe existir después de regenerar", regeneratedBuilderFile)

        val builderPsiFile = PsiManager.getInstance(project).findFile(regeneratedBuilderFile).requireIs<PsiJavaFile>()
        val builderClass = builderPsiFile.classes.first()

        // Verificar nuevo campo
        val fields = builderClass.fields
        assertTrue("Debe tener campo customerName", fields.any { it.name == "customerName" })

        // Verificar nuevo method
        val methods = builderClass.methods
        assertTrue("Debe tener método withCustomerName", methods.any { it.name == "withCustomerName" })
    }

    /**
     * Test: Generar inner builder
     * Escenario: Usuario elige generar el builder como clase interna estática
     * Resultado esperado: Builder se genera dentro de la clase original
     */
    fun testGenerateInnerBuilder() {
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

        myFixture.openFileInEditor(productFile.virtualFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val productClass = psiFile.classes.first()

        // ACT: Generar inner builder
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

            BuilderWriter.writeBuilder(context, null)
        }

        // ASSERT: Verificar que se creó el inner builder
        val updatedPsiFile = PsiManager.getInstance(project).findFile(productFile.virtualFile).requireIs<PsiJavaFile>()
        val updatedProductClass = updatedPsiFile.classes.first()

        val innerClasses = updatedProductClass.innerClasses
        assertTrue("Debe tener al menos una clase interna", innerClasses.isNotEmpty())

        val builderClass = innerClasses.firstOrNull { it.name == "Builder" }
        assertNotNull("Debe existir clase interna Builder", builderClass)
        assertTrue("El inner builder debe ser static", builderClass!!.hasModifierProperty("static"))
        assertTrue("El inner builder debe ser final", builderClass.hasModifierProperty("final"))

        // Verificar métodos del inner builder
        val methods = builderClass.methods
        assertTrue("Debe tener método withName", methods.any { it.name == "withName" })
        assertTrue("Debe tener método withPrice", methods.any { it.name == "withPrice" })
        assertTrue("Debe tener método build", methods.any { it.name == "build" })
    }

    /**
     * Test: Generar builder con method "but"
     * Escenario: Usuario habilita la opción de generar method "but" para copiar builder
     * Resultado esperado: Builder incluye method but()
     */
    fun testGenerateBuilderWithButMethod() {
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

        myFixture.openFileInEditor(personFile.virtualFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val personClass = psiFile.classes.first()

        // ACT: Generar builder con method but
        WriteCommandAction.runWriteCommandAction(project) {
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

            BuilderWriter.writeBuilder(context, null)
        }

        // ASSERT: Verificar method but
        val builderFile = myFixture.findFileInTempDir("PersonBuilder.java")
        assertNotNull("Debe existir PersonBuilder.java", builderFile)

        val builderPsiFile = PsiManager.getInstance(project).findFile(builderFile).requireIs<PsiJavaFile>()
        val builderClass = builderPsiFile.classes.first()

        val methods = builderClass.methods
        val butMethod = methods.firstOrNull { it.name == "but" }
        assertNotNull("Debe tener método but", butMethod)

        // Verificar que el method but recibe un Person como parámetro
        val parameters = butMethod!!.parameterList.parameters
        assertEquals("El método but no debe tener parámetro", 0, parameters.size)
    }

    /**
     * Test: Generar builder con copy constructor
     * Escenario: Usuario habilita la opción de copy constructor
     * Resultado esperado: Builder incluye constructor público que acepta instancia existente
     */
    fun testGenerateBuilderWithCopyConstructor() {
        // ARRANGE: Crear clase
        val userFile = myFixture.addFileToProject(
            "User.java",
            """
            package com.example;

            public class User {
                private String username;
                private String email;

                public User(String username, String email) {
                    this.username = username;
                    this.email = email;
                }

                public String getUsername() { return username; }
                public String getEmail() { return email; }
            }
            """.trimIndent(),
        )

        myFixture.openFileInEditor(userFile.virtualFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val userClass = psiFile.classes.first()

        // ACT: Generar builder con copy constructor
        WriteCommandAction.runWriteCommandAction(project) {
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
                hasAddCopyConstructor = true,
            )

            BuilderWriter.writeBuilder(context, null)
        }

        // ASSERT: Verificar copy constructor
        val builderFile = myFixture.findFileInTempDir("UserBuilder.java")
        assertNotNull("Debe existir UserBuilder.java", builderFile)

        val builderPsiFile = PsiManager.getInstance(project).findFile(builderFile).requireIs<PsiJavaFile>()
        val builderClass = builderPsiFile.classes.first()

        val constructors = builderClass.constructors
        assertTrue("Debe tener al menos 2 constructores", constructors.size >= 2)

        // Verificar que existe constructor público (copy constructor)
        val publicConstructor = constructors.firstOrNull { it.hasModifierProperty("public") }
        assertNotNull("Debe tener un constructor público", publicConstructor)

        // Verificar que el copy constructor recibe User como parámetro
        val copyConstructor = constructors.firstOrNull {
            it.parameterList.parameters.any { param -> param.type.presentableText == "User" }
        }
        assertNotNull("Debe tener copy constructor que recibe User", copyConstructor)
    }

    /**
     * Test: Generar builder con useSingleField
     * Escenario: Usuario habilita useSingleField (usa instancia del objeto en lugar de campos separados)
     * Resultado esperado: Builder tiene un solo campo del tipo de la clase original
     */
    fun testGenerateBuilderWithSingleField() {
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

        myFixture.openFileInEditor(itemFile.virtualFile)
        val psiFile = myFixture.file.requireIs<PsiJavaFile>()
        val itemClass = psiFile.classes.first()

        // ACT: Generar builder con useSingleField
        WriteCommandAction.runWriteCommandAction(project) {
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

            BuilderWriter.writeBuilder(context, null)
        }

        // ASSERT: Verificar que usa un solo campo
        val builderFile = myFixture.findFileInTempDir("ItemBuilder.java")
        assertNotNull("Debe existir ItemBuilder.java", builderFile)

        val builderPsiFile = PsiManager.getInstance(project).findFile(builderFile).requireIs<PsiJavaFile>()
        val builderClass = builderPsiFile.classes.first()

        val fields = builderClass.fields
        assertEquals("Debe tener exactamente 1 campo (el objeto Item)", 1, fields.size)
        assertEquals("Item", fields[0].type.presentableText)

        // Verificar que los métodos withXxx modifican el objeto directamente
        val methods = builderClass.methods
        assertTrue("Debe tener método withCode", methods.any { it.name == "withCode" })
        assertTrue("Debe tener método withQuantity", methods.any { it.name == "withQuantity" })
    }
}
