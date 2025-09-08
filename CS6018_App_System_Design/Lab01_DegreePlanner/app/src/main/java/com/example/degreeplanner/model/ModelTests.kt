// ModelTests.kt - Custom testing for Model package
// Put this in: app/src/test/java/com/example/degreeplanner/model/ModelTests.kt

package com.example.degreeplanner.model

// Simple custom testing framework
object TestRunner {
    private var passCount = 0
    private var failCount = 0
    private var currentTest = ""
    
    fun test(testName: String, testFunction: () -> Unit) {
        currentTest = testName
        try {
            testFunction()
            passCount++
            println("✅ PASS: $testName")
        } catch (e: AssertionError) {
            failCount++
            println("❌ FAIL: $testName - ${e.message}")
        } catch (e: Exception) {
            failCount++
            println("💥 ERROR: $testName - ${e.message}")
        }
    }
    
    fun assert(condition: Boolean, message: String) {
        if (!condition) {
            throw AssertionError(message)
        }
    }
    
    fun assertEqual(expected: Any?, actual: Any?, message: String = "Values should be equal") {
        if (expected != actual) {
            throw AssertionError("$message\nExpected: $expected\nActual: $actual")
        }
    }
    
    fun assertNotEqual(unexpected: Any?, actual: Any?, message: String = "Values should not be equal") {
        if (unexpected == actual) {
            throw AssertionError("$message\nUnexpected: $unexpected\nActual: $actual")
        }
    }
    
    fun assertTrue(condition: Boolean, message: String = "Should be true") {
        assert(condition, message)
    }
    
    fun assertFalse(condition: Boolean, message: String = "Should be false") {
        assert(!condition, message)
    }
    
    fun printResults() {
        println("\n" + "=".repeat(50))
        println("📊 MODEL PACKAGE TEST RESULTS")
        println("=".repeat(50))
        println("✅ Passed: $passCount")
        println("❌ Failed: $failCount")
        println("📈 Total:  ${passCount + failCount}")
        if (failCount == 0) {
            println("🎉 All tests passed!")
        }
        println("=".repeat(50))
    }
}

/**
 * Tests for Course data class
 */
object CourseTests {
    fun runAllTests() {
        println("\n🔬 Running Course Tests...")
        
        TestRunner.test("Course toString formats correctly") {
            val course = Course("CS", 101)
            TestRunner.assertEqual("CS 101", course.toString())
        }
        
        TestRunner.test("Course equality works with same values") {
            val course1 = Course("CS", 101)
            val course2 = Course("CS", 101)
            TestRunner.assertEqual(course1, course2)
        }
        
        TestRunner.test("Course inequality works with different departments") {
            val course1 = Course("CS", 101)
            val course2 = Course("MATH", 101)
            TestRunner.assertNotEqual(course1, course2)
        }
        
        TestRunner.test("Course inequality works with different numbers") {
            val course1 = Course("CS", 101)
            val course2 = Course("CS", 201)
            TestRunner.assertNotEqual(course1, course2)
        }
        
        TestRunner.test("Course with empty department") {
            val course = Course("", 101)
            TestRunner.assertEqual(" 101", course.toString())
        }
        
        TestRunner.test("Course with zero number") {
            val course = Course("CS", 0)
            TestRunner.assertEqual("CS 0", course.toString())
        }
    }
}

/**
 * Tests for Requirement sealed class
 */
object RequirementTests {
    fun runAllTests() {
        println("\n🔬 Running Requirement Tests...")
        
        TestRunner.test("SpecificCourse description is correct") {
            val requirement = Requirement.SpecificCourse(Course("CS", 101))
            TestRunner.assertEqual("Take CS 101", requirement.description)
        }
        
        TestRunner.test("OneOf description with two courses") {
            val courses = listOf(Course("PHIL", 101), Course("SOC", 101))
            val requirement = Requirement.OneOf(courses)
            TestRunner.assertEqual("Take one of: PHIL 101 or SOC 101", requirement.description)
        }
        
        TestRunner.test("OneOf description with single course") {
            val courses = listOf(Course("PHIL", 101))
            val requirement = Requirement.OneOf(courses)
            TestRunner.assertEqual("Take one of: PHIL 101", requirement.description)
        }
        
        TestRunner.test("OneOf description with three courses") {
            val courses = listOf(Course("PHIL", 101), Course("SOC", 101), Course("HIST", 101))
            val requirement = Requirement.OneOf(courses)
            TestRunner.assertEqual("Take one of: PHIL 101 or SOC 101 or HIST 101", requirement.description)
        }
        
        TestRunner.test("SpecificCourse with different course") {
            val requirement = Requirement.SpecificCourse(Course("MATH", 201))
            TestRunner.assertEqual("Take MATH 201", requirement.description)
        }
    }
}

/**
 * Tests for RequirementChecker business logic
 */
object RequirementCheckerTests {
    fun runAllTests() {
        println("\n🔬 Running RequirementChecker Tests...")
        
        // Test data
        val cs101 = Course("CS", 101)
        val math101 = Course("MATH", 101)
        val phil101 = Course("PHIL", 101)
        val soc101 = Course("SOC", 101)
        
        val csRequirement = Requirement.SpecificCourse(cs101)
        val philOrSocRequirement = Requirement.OneOf(listOf(phil101, soc101))
        
        // SpecificCourse tests
        TestRunner.test("SpecificCourse satisfied when course is taken") {
            val courses = listOf(cs101)
            TestRunner.assertTrue(
                RequirementChecker.isSatisfied(csRequirement, courses),
                "CS 101 requirement should be satisfied when CS 101 is taken"
            )
        }
        
        TestRunner.test("SpecificCourse not satisfied when course is not taken") {
            val courses = listOf(math101)
            TestRunner.assertFalse(
                RequirementChecker.isSatisfied(csRequirement, courses),
                "CS 101 requirement should not be satisfied when only MATH 101 is taken"
            )
        }
        
        TestRunner.test("SpecificCourse not satisfied with empty course list") {
            val courses = emptyList<Course>()
            TestRunner.assertFalse(
                RequirementChecker.isSatisfied(csRequirement, courses),
                "CS 101 requirement should not be satisfied with no courses"
            )
        }
        
        // OneOf tests
        TestRunner.test("OneOf satisfied with first option") {
            val courses = listOf(phil101)
            TestRunner.assertTrue(
                RequirementChecker.isSatisfied(philOrSocRequirement, courses),
                "PHIL or SOC requirement should be satisfied with PHIL 101"
            )
        }
        
        TestRunner.test("OneOf satisfied with second option") {
            val courses = listOf(soc101)
            TestRunner.assertTrue(
                RequirementChecker.isSatisfied(philOrSocRequirement, courses),
                "PHIL or SOC requirement should be satisfied with SOC 101"
            )
        }
        
        TestRunner.test("OneOf satisfied with both options") {
            val courses = listOf(phil101, soc101)
            TestRunner.assertTrue(
                RequirementChecker.isSatisfied(philOrSocRequirement, courses),
                "PHIL or SOC requirement should be satisfied with both courses"
            )
        }
        
        TestRunner.test("OneOf not satisfied with neither option") {
            val courses = listOf(math101)
            TestRunner.assertFalse(
                RequirementChecker.isSatisfied(philOrSocRequirement, courses),
                "PHIL or SOC requirement should not be satisfied with MATH 101"
            )
        }
        
        // AllSatisfied tests
        TestRunner.test("All requirements satisfied with complete course list") {
            val requirements = listOf(csRequirement, philOrSocRequirement)
            val courses = listOf(cs101, phil101)
            TestRunner.assertTrue(
                RequirementChecker.allSatisfied(requirements, courses),
                "All requirements should be satisfied with CS 101 and PHIL 101"
            )
        }
        
        TestRunner.test("All requirements satisfied with alternative choice") {
            val requirements = listOf(csRequirement, philOrSocRequirement)
            val courses = listOf(cs101, soc101)
            TestRunner.assertTrue(
                RequirementChecker.allSatisfied(requirements, courses),
                "All requirements should be satisfied with CS 101 and SOC 101"
            )
        }
        
        TestRunner.test("Not all requirements satisfied with missing CS") {
            val requirements = listOf(csRequirement, philOrSocRequirement)
            val courses = listOf(phil101)
            TestRunner.assertFalse(
                RequirementChecker.allSatisfied(requirements, courses),
                "All requirements should not be satisfied with only PHIL 101"
            )
        }
        
        TestRunner.test("Not all requirements satisfied with missing PHIL/SOC") {
            val requirements = listOf(csRequirement, philOrSocRequirement)
            val courses = listOf(cs101)
            TestRunner.assertFalse(
                RequirementChecker.allSatisfied(requirements, courses),
                "All requirements should not be satisfied with only CS 101"
            )
        }
        
        TestRunner.test("Empty requirements always satisfied") {
            val requirements = emptyList<Requirement>()
            val courses = listOf(cs101)
            TestRunner.assertTrue(
                RequirementChecker.allSatisfied(requirements, courses),
                "Empty requirements should always be satisfied"
            )
        }
        
        TestRunner.test("Requirements not satisfied with no courses") {
            val requirements = listOf(csRequirement, philOrSocRequirement)
            val courses = emptyList<Course>()
            TestRunner.assertFalse(
                RequirementChecker.allSatisfied(requirements, courses),
                "Requirements should not be satisfied with no courses"
            )
        }
        
        TestRunner.test("Extra courses don't break satisfaction") {
            val requirements = listOf(csRequirement, philOrSocRequirement)
            val courses = listOf(cs101, phil101, soc101, math101, Course("HIST", 101))
            TestRunner.assertTrue(
                RequirementChecker.allSatisfied(requirements, courses),
                "Requirements should be satisfied even with extra courses"
            )
        }
    }
}

/**
 * Main function to run all model tests
 */
fun main() {
    println("🚀 Starting Model Package Tests")
    
    CourseTests.runAllTests()
    RequirementTests.runAllTests()
    RequirementCheckerTests.runAllTests()
    
    TestRunner.printResults()
}

/**
 * Function to run tests from Android app (if needed)
 */
fun runModelTests() {
    main()
}