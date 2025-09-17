package com.example.degreeplanner.viewmodel

import com.example.degreeplanner.model.Course
import com.example.degreeplanner.model.Requirement

object ViewModelTestRunner {
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
    
    fun assertEqual(expected: Any?, actual: Any?, message: String = "Values should be equal") {
        if (expected != actual) {
            throw AssertionError("$message\nExpected: $expected\nActual: $actual")
        }
    }
    
    fun assertTrue(condition: Boolean, message: String = "Should be true") {
        if (!condition) {
            throw AssertionError(message)
        }
    }
    
    fun assertFalse(condition: Boolean, message: String = "Should be false") {
        if (condition) {
            throw AssertionError(message)
        }
    }
    
    fun assertContains(list: List<Any>, item: Any, message: String = "List should contain item") {
        if (!list.contains(item)) {
            throw AssertionError("$message\nList: $list\nMissing item: $item")
        }
    }
    
    fun assertNotContains(list: List<Any>, item: Any, message: String = "List should not contain item") {
        if (list.contains(item)) {
            throw AssertionError("$message\nList: $list\nUnexpected item: $item")
        }
    }
    
    fun printResults() {
        println("\n" + "=".repeat(50))
        println("📊 VIEWMODEL PACKAGE TEST RESULTS")
        println("=".repeat(50))
        println("✅ Passed: $passCount")
        println("❌ Failed: $failCount")
        println("📈 Total:  ${passCount + failCount}")
        if (failCount == 0) {
            println("🎉 All ViewModel tests passed!")
        }
        println("=".repeat(50))
    }
}

/**
 * Tests for DegreePlannerViewModel
 */
object DegreePlannerViewModelTests {
    
    private fun createFreshViewModel(): DegreePlannerViewModel {
        return DegreePlannerViewModel()
    }
    
    fun runAllTests() {
        println("\n🔬 Running DegreePlannerViewModel Tests...")
        
        // Test data
        val cs101 = Course("CS", 101)
        val phil101 = Course("PHIL", 101)
        val soc101 = Course("SOC", 101)
        val math101 = Course("MATH", 101)
        
        // Initial state tests
        ViewModelTestRunner.test("Initial state has empty course list") {
            val viewModel = createFreshViewModel()
            ViewModelTestRunner.assertEqual(0, viewModel.courses.size, "Should start with no courses")
        }
        
        ViewModelTestRunner.test("Initial state has correct requirements") {
            val viewModel = createFreshViewModel()
            ViewModelTestRunner.assertEqual(2, viewModel.requirements.size, "Should have 2 requirements")
            
            // Check first requirement is CS 101
            val firstReq = viewModel.requirements[0] as Requirement.SpecificCourse
            ViewModelTestRunner.assertEqual(cs101, firstReq.course, "First requirement should be CS 101")
            
            // Check second requirement is PHIL/SOC choice
            val secondReq = viewModel.requirements[1] as Requirement.OneOf
            ViewModelTestRunner.assertTrue(
                secondReq.courses.contains(phil101) && secondReq.courses.contains(soc101),
                "Second requirement should include PHIL 101 and SOC 101"
            )
        }
        
        ViewModelTestRunner.test("Initial state is not complete") {
            val viewModel = createFreshViewModel()
            ViewModelTestRunner.assertFalse(viewModel.isComplete(), "Should not be complete initially")
        }
        
        // Add course tests
        ViewModelTestRunner.test("Can add single course") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            
            ViewModelTestRunner.assertEqual(1, viewModel.courses.size, "Should have 1 course")
            ViewModelTestRunner.assertContains(viewModel.courses, cs101, "Should contain CS 101")
        }
        
        ViewModelTestRunner.test("Can add multiple different courses") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            
            ViewModelTestRunner.assertEqual(2, viewModel.courses.size, "Should have 2 courses")
            ViewModelTestRunner.assertContains(viewModel.courses, cs101, "Should contain CS 101")
            ViewModelTestRunner.assertContains(viewModel.courses, phil101, "Should contain PHIL 101")
        }
        
        ViewModelTestRunner.test("Cannot add duplicate courses") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(cs101) // Try to add same course again
            
            ViewModelTestRunner.assertEqual(1, viewModel.courses.size, "Should still have only 1 course")
        }
        
        ViewModelTestRunner.test("Can add many courses without duplicates") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            viewModel.addCourse(cs101) // duplicate
            viewModel.addCourse(soc101)
            viewModel.addCourse(phil101) // duplicate
            viewModel.addCourse(math101)
            
            ViewModelTestRunner.assertEqual(4, viewModel.courses.size, "Should have 4 unique courses")
        }
        
        // Remove course tests
        ViewModelTestRunner.test("Can remove existing course") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            
            viewModel.removeCourse(cs101)
            
            ViewModelTestRunner.assertEqual(1, viewModel.courses.size, "Should have 1 course left")
            ViewModelTestRunner.assertNotContains(viewModel.courses, cs101, "Should not contain CS 101")
            ViewModelTestRunner.assertContains(viewModel.courses, phil101, "Should still contain PHIL 101")
        }
        
        ViewModelTestRunner.test("Removing non-existent course does nothing") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            
            viewModel.removeCourse(phil101) // Course not in list
            
            ViewModelTestRunner.assertEqual(1, viewModel.courses.size, "Should still have 1 course")
            ViewModelTestRunner.assertContains(viewModel.courses, cs101, "Should still contain CS 101")
        }
        
        ViewModelTestRunner.test("Can remove all courses") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            
            viewModel.removeCourse(cs101)
            viewModel.removeCourse(phil101)
            
            ViewModelTestRunner.assertEqual(0, viewModel.courses.size, "Should have no courses")
        }
        
        // Requirement satisfaction tests
        ViewModelTestRunner.test("CS requirement satisfied when CS 101 added") {
            val viewModel = createFreshViewModel()
            val csRequirement = viewModel.requirements[0]
            
            ViewModelTestRunner.assertFalse(
                viewModel.isRequirementSatisfied(csRequirement),
                "CS requirement should not be satisfied initially"
            )
            
            viewModel.addCourse(cs101)
            
            ViewModelTestRunner.assertTrue(
                viewModel.isRequirementSatisfied(csRequirement),
                "CS requirement should be satisfied after adding CS 101"
            )
        }
        
        ViewModelTestRunner.test("PHIL/SOC requirement satisfied with PHIL 101") {
            val viewModel = createFreshViewModel()
            val philSocRequirement = viewModel.requirements[1]
            
            ViewModelTestRunner.assertFalse(
                viewModel.isRequirementSatisfied(philSocRequirement),
                "PHIL/SOC requirement should not be satisfied initially"
            )
            
            viewModel.addCourse(phil101)
            
            ViewModelTestRunner.assertTrue(
                viewModel.isRequirementSatisfied(philSocRequirement),
                "PHIL/SOC requirement should be satisfied after adding PHIL 101"
            )
        }
        
        ViewModelTestRunner.test("PHIL/SOC requirement satisfied with SOC 101") {
            val viewModel = createFreshViewModel()
            val philSocRequirement = viewModel.requirements[1]
            
            viewModel.addCourse(soc101)
            
            ViewModelTestRunner.assertTrue(
                viewModel.isRequirementSatisfied(philSocRequirement),
                "PHIL/SOC requirement should be satisfied after adding SOC 101"
            )
        }
        
        // Completion tests
        ViewModelTestRunner.test("Not complete with only CS 101") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            
            ViewModelTestRunner.assertFalse(viewModel.isComplete(), "Should not be complete with only CS 101")
        }
        
        ViewModelTestRunner.test("Not complete with only PHIL 101") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(phil101)
            
            ViewModelTestRunner.assertFalse(viewModel.isComplete(), "Should not be complete with only PHIL 101")
        }
        
        ViewModelTestRunner.test("Complete with CS 101 and PHIL 101") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            
            ViewModelTestRunner.assertTrue(
                viewModel.isComplete(),
                "Should be complete with CS 101 and PHIL 101"
            )
        }
        
        ViewModelTestRunner.test("Complete with CS 101 and SOC 101") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(soc101)
            
            ViewModelTestRunner.assertTrue(
                viewModel.isComplete(),
                "Should be complete with CS 101 and SOC 101"
            )
        }
        
        ViewModelTestRunner.test("Still complete with extra courses") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            viewModel.addCourse(soc101) // Extra
            viewModel.addCourse(math101) // Extra
            
            ViewModelTestRunner.assertTrue(
                viewModel.isComplete(),
                "Should still be complete with extra courses"
            )
        }
        
        ViewModelTestRunner.test("Becomes incomplete when required course removed") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            
            ViewModelTestRunner.assertTrue(viewModel.isComplete(), "Should be complete initially")
            
            viewModel.removeCourse(cs101)
            
            ViewModelTestRunner.assertFalse(
                viewModel.isComplete(),
                "Should become incomplete after removing CS 101"
            )
        }
        
        // State persistence tests
        ViewModelTestRunner.test("Course order is maintained") {
            val viewModel = createFreshViewModel()
            viewModel.addCourse(phil101)
            viewModel.addCourse(cs101)
            viewModel.addCourse(soc101)
            
            ViewModelTestRunner.assertEqual(phil101, viewModel.courses[0], "First course should be PHIL 101")
            ViewModelTestRunner.assertEqual(cs101, viewModel.courses[1], "Second course should be CS 101")
            ViewModelTestRunner.assertEqual(soc101, viewModel.courses[2], "Third course should be SOC 101")
        }
        
        ViewModelTestRunner.test("Multiple add/remove operations work correctly") {
            val viewModel = createFreshViewModel()
            
            // Add some courses
            viewModel.addCourse(cs101)
            viewModel.addCourse(phil101)
            ViewModelTestRunner.assertEqual(2, viewModel.courses.size)
            ViewModelTestRunner.assertTrue(viewModel.isComplete())
            
            // Remove one
            viewModel.removeCourse(phil101)
            ViewModelTestRunner.assertEqual(1, viewModel.courses.size)
            ViewModelTestRunner.assertFalse(viewModel.isComplete())
            
            // Add different one
            viewModel.addCourse(soc101)
            ViewModelTestRunner.assertEqual(2, viewModel.courses.size)
            ViewModelTestRunner.assertTrue(viewModel.isComplete())
            
            // Add original back
            viewModel.addCourse(phil101)
            ViewModelTestRunner.assertEqual(3, viewModel.courses.size)
            ViewModelTestRunner.assertTrue(viewModel.isComplete())
        }
    }
}

/**
 * Main function to run all ViewModel tests
 */
fun main() {
    println("🚀 Starting ViewModel Package Tests")
    
    DegreePlannerViewModelTests.runAllTests()
    
    ViewModelTestRunner.printResults()
}

/**
 * Function to run tests from Android app (if needed)
 */
fun runViewModelTests() {
    main()
}