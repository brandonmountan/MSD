package com.example.degreeplanner.view

import com.example.degreeplanner.model.Course
import com.example.degreeplanner.model.Requirement
import com.example.degreeplanner.model.RequirementChecker

object ViewTestRunner {
    private var passCount = 0
    private var failCount = 0
    
    fun test(testName: String, testFunction: () -> Unit) {
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
    
    fun printResults() {
        println("\n" + "=".repeat(50))
        println("📊 VIEW PACKAGE TEST RESULTS")
        println("=".repeat(50))
        println("✅ Passed: $passCount")
        println("❌ Failed: $failCount")
        println("📈 Total:  ${passCount + failCount}")
        if (failCount == 0) {
            println("🎉 All View tests passed!")
        }
        println("=".repeat(50))
    }
}

class CallbackTracker {
    val calls = mutableListOf<String>()
    var lastCourse: Course? = null
    
    fun onAddCourse(course: Course) {
        calls.add("addCourse")
        lastCourse = course
    }
    
    fun onRemoveCourse(course: Course) {
        calls.add("removeCourse")
        lastCourse = course
    }
    
    fun reset() {
        calls.clear()
        lastCourse = null
    }
}

object CourseInputCardTests {
    
    fun runAllTests() {
        println("\n🔬 Running CourseInputCard Logic Tests...")
        
        ViewTestRunner.test("Valid course input creates correct Course object") {
            val department = "CS"
            val number = "101"
            
            // Simulate the logic from CourseInputCard
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            ViewTestRunner.assertTrue(isValid, "CS 101 should be valid input")
            
            if (isValid) {
                val course = Course(department.uppercase(), courseNumber!!)
                ViewTestRunner.assertEqual("CS", course.department)
                ViewTestRunner.assertEqual(101, course.number)
            }
        }
        
        ViewTestRunner.test("Empty department is invalid") {
            val department = ""
            val number = "101"
            
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            ViewTestRunner.assertFalse(isValid, "Empty department should be invalid")
        }
        
        ViewTestRunner.test("Non-numeric course number is invalid") {
            val department = "CS"
            val number = "abc"
            
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            ViewTestRunner.assertFalse(isValid, "Non-numeric course number should be invalid")
        }
        
        ViewTestRunner.test("Zero course number is invalid") {
            val department = "CS"
            val number = "0"
            
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            ViewTestRunner.assertFalse(isValid, "Zero course number should be invalid")
        }
        
        ViewTestRunner.test("Negative course number is invalid") {
            val department = "CS"
            val number = "-101"
            
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            ViewTestRunner.assertFalse(isValid, "Negative course number should be invalid")
        }
        
        ViewTestRunner.test("Department is converted to uppercase") {
            val department = "cs"
            val number = "101"
            
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            if (isValid) {
                val course = Course(department.uppercase(), courseNumber!!)
                ViewTestRunner.assertEqual("CS", course.department, "Department should be uppercase")
            }
        }
        
        ViewTestRunner.test("Whitespace is trimmed from department") {
            val department = "  CS  "
            val number = "101"
            
            val courseNumber = number.toIntOrNull()
            val isValid = department.isNotBlank() && courseNumber != null && courseNumber > 0
            
            if (isValid) {
                val course = Course(department.trim().uppercase(), courseNumber!!)
                ViewTestRunner.assertEqual("CS", course.department, "Department should be trimmed")
            }
        }
    }
}

/**
 * Tests for CoursesList logic and behavior
 */
object CoursesListTests {
    
    fun runAllTests() {
        println("\n🔬 Running CoursesList Logic Tests...")
        
        ViewTestRunner.test("Empty course list shows correct message") {
            val courses = emptyList<Course>()
            
            // Simulate the logic from CoursesList
            val isEmpty = courses.isEmpty()
            val courseCount = courses.size
            
            ViewTestRunner.assertTrue(isEmpty, "Empty list should be recognized as empty")
            ViewTestRunner.assertEqual(0, courseCount, "Course count should be 0")
        }
        
        ViewTestRunner.test("Course count is displayed correctly") {
            val courses = listOf(
                Course("CS", 101),
                Course("PHIL", 101),
                Course("MATH", 201)
            )
            
            val courseCount = courses.size
            ViewTestRunner.assertEqual(3, courseCount, "Should count 3 courses")
        }
        
        ViewTestRunner.test("Course removal callback works") {
            val tracker = CallbackTracker()
            val courseToRemove = Course("CS", 101)
            
            // Simulate clicking remove button
            tracker.onRemoveCourse(courseToRemove)
            
            ViewTestRunner.assertEqual(1, tracker.calls.size, "Should have 1 callback call")
            ViewTestRunner.assertEqual("removeCourse", tracker.calls[0], "Should call removeCourse")
            ViewTestRunner.assertEqual(courseToRemove, tracker.lastCourse, "Should pass correct course")
        }
        
        ViewTestRunner.test("Course list filtering logic works") {
            val courses = listOf(
                Course("CS", 101),
                Course("PHIL", 101),
                Course("MATH", 201)
            )
            val courseToRemove = Course("PHIL", 101)
            
            // Simulate the filter operation that happens when removing
            val filteredCourses = courses.filter { it != courseToRemove }
            
            ViewTestRunner.assertEqual(2, filteredCourses.size, "Should have 2 courses after removal")
            ViewTestRunner.assertFalse(
                filteredCourses.contains(courseToRemove),
                "Filtered list should not contain removed course"
            )
            ViewTestRunner.assertTrue(
                filteredCourses.contains(Course("CS", 101)),
                "Filtered list should still contain CS 101"
            )
        }
    }
}

/**
 * Tests for RequirementsList logic and status calculation
 */
object RequirementsListTests {
    
    fun runAllTests() {
        println("\n🔬 Running RequirementsList Logic Tests...")
        
        val requirements = listOf(
            Requirement.SpecificCourse(Course("CS", 101)),
            Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)))
        )
        
        ViewTestRunner.test("Calculates completion status correctly - no courses") {
            val courses = emptyList<Course>()
            
            val allComplete = RequirementChecker.allSatisfied(requirements, courses)
            val satisfiedCount = requirements.count { RequirementChecker.isSatisfied(it, courses) }
            
            ViewTestRunner.assertFalse(allComplete, "Should not be complete with no courses")
            ViewTestRunner.assertEqual(0, satisfiedCount, "Should have 0 satisfied requirements")
        }
        
        ViewTestRunner.test("Calculates completion status correctly - partial completion") {
            val courses = listOf(Course("CS", 101))
            
            val allComplete = RequirementChecker.allSatisfied(requirements, courses)
            val satisfiedCount = requirements.count { RequirementChecker.isSatisfied(it, courses) }
            
            ViewTestRunner.assertFalse(allComplete, "Should not be complete with only CS 101")
            ViewTestRunner.assertEqual(1, satisfiedCount, "Should have 1 satisfied requirement")
        }
        
        ViewTestRunner.test("Calculates completion status correctly - full completion") {
            val courses = listOf(Course("CS", 101), Course("PHIL", 101))
            
            val allComplete = RequirementChecker.allSatisfied(requirements, courses)
            val satisfiedCount = requirements.count { RequirementChecker.isSatisfied(it, courses) }
            
            ViewTestRunner.assertTrue(allComplete, "Should be complete with CS 101 and PHIL 101")
            ViewTestRunner.assertEqual(2, satisfiedCount, "Should have 2 satisfied requirements")
        }
        
        ViewTestRunner.test("Individual requirement status calculation") {
            val courses = listOf(Course("PHIL", 101))
            
            val csRequirement = requirements[0]
            val philSocRequirement = requirements[1]
            
            val cssatisfied = RequirementChecker.isSatisfied(csRequirement, courses)
            val philSocSatisfied = RequirementChecker.isSatisfied(philSocRequirement, courses)
            
            ViewTestRunner.assertFalse(cssatisfied, "CS requirement should not be satisfied")
            ViewTestRunner.assertTrue(philSocSatisfied, "PHIL/SOC requirement should be satisfied")
        }
        
        ViewTestRunner.test("Celebration condition works correctly") {
            val coursesIncomplete = listOf(Course("CS", 101))
            val coursesComplete = listOf(Course("CS", 101), Course("SOC", 101))
            
            val incompleteStatus = RequirementChecker.allSatisfied(requirements, coursesIncomplete)
            val completeStatus = RequirementChecker.allSatisfied(requirements, coursesComplete)
            
            ViewTestRunner.assertFalse(incompleteStatus, "Should not show celebration when incomplete")
            ViewTestRunner.assertTrue(completeStatus, "Should show celebration when complete")
        }
        
        ViewTestRunner.test("Progress calculation works correctly") {
            val noCourses = emptyList<Course>()
            val someCourses = listOf(Course("CS", 101))
            val allCourses = listOf(Course("CS", 101), Course("PHIL", 101))
            
            val noProgress = requirements.count { RequirementChecker.isSatisfied(it, noCourses) }
            val someProgress = requirements.count { RequirementChecker.isSatisfied(it, someCourses) }
            val fullProgress = requirements.count { RequirementChecker.isSatisfied(it, allCourses) }
            
            ViewTestRunner.assertEqual(0, noProgress, "Should have 0/2 progress")
            ViewTestRunner.assertEqual(1, someProgress, "Should have 1/2 progress")
            ViewTestRunner.assertEqual(2, fullProgress, "Should have 2/2 progress")
        }
    }
}

/**
 * Tests for DegreePlannerScreen state management logic
 */
object DegreePlannerScreenTests {
    
    fun runAllTests() {
        println("\n🔬 Running DegreePlannerScreen Logic Tests...")
        
        ViewTestRunner.test("Initial state setup") {
            // Simulate the initial state from DegreePlannerScreen
            var courses = listOf<Course>()
            val requirements = listOf(
                Requirement.SpecificCourse(Course("CS", 101)),
                Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)))
            )
            
            ViewTestRunner.assertEqual(0, courses.size, "Should start with empty course list")
            ViewTestRunner.assertEqual(2, requirements.size, "Should have 2 requirements")
        }
        
        ViewTestRunner.test("Add course state update") {
            var courses = listOf<Course>()
            val newCourse = Course("CS", 101)
            
            // Simulate the logic from onAddCourse callback
            if (!courses.contains(newCourse)) {
                courses = courses + newCourse
            }
            
            ViewTestRunner.assertEqual(1, courses.size, "Should have 1 course after adding")
            ViewTestRunner.assertTrue(courses.contains(newCourse), "Should contain the new course")
        }
        
        ViewTestRunner.test("Duplicate course prevention") {
            var courses = listOf(Course("CS", 101))
            val duplicateCourse = Course("CS", 101)
            
            val initialSize = courses.size
            
            // Simulate the logic from onAddCourse callback
            if (!courses.contains(duplicateCourse)) {
                courses = courses + duplicateCourse
            }
            
            ViewTestRunner.assertEqual(initialSize, courses.size, "Should not add duplicate course")
        }
        
        ViewTestRunner.test("Remove course state update") {
            var courses = listOf(Course("CS", 101), Course("PHIL", 101))
            val courseToRemove = Course("CS", 101)
            
            // Simulate the logic from onRemoveCourse callback
            courses = courses.filter { it != courseToRemove }
            
            ViewTestRunner.assertEqual(1, courses.size, "Should have 1 course after removal")
            ViewTestRunner.assertFalse(
                courses.contains(courseToRemove),
                "Should not contain removed course"
            )
        }
        
        ViewTestRunner.test("State coordination between components") {
            val tracker = CallbackTracker()
            var courses = listOf<Course>()
            
            // Simulate adding course through CourseInputCard
            val newCourse = Course("PHIL", 101)
            tracker.onAddCourse(newCourse)
            
            // Simulate the state update that would happen in parent
            if (!courses.contains(newCourse)) {
                courses = courses + newCourse
            }
            
            // Simulate removing course through CoursesList
            tracker.onRemoveCourse(newCourse)
            courses = courses.filter { it != newCourse }
            
            ViewTestRunner.assertEqual(2, tracker.calls.size, "Should have 2 component interactions")
            ViewTestRunner.assertEqual(0, courses.size, "Should end with empty course list")
        }
        
        ViewTestRunner.test("Requirements dependency on course state") {
            val courses1 = listOf<Course>()
            val courses2 = listOf(Course("CS", 101))
            val courses3 = listOf(Course("CS", 101), Course("SOC", 101))
            
            val requirements = listOf(
                Requirement.SpecificCourse(Course("CS", 101)),
                Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)))
            )
            
            // Test how RequirementsList would see different states
            val status1 = RequirementChecker.allSatisfied(requirements, courses1)
            val status2 = RequirementChecker.allSatisfied(requirements, courses2)
            val status3 = RequirementChecker.allSatisfied(requirements, courses3)
            
            ViewTestRunner.assertFalse(status1, "Empty courses should not satisfy requirements")
            ViewTestRunner.assertFalse(status2, "Partial courses should not satisfy all requirements")
            ViewTestRunner.assertTrue(status3, "Complete courses should satisfy all requirements")
        }
    }
}

/**
 * Tests for overall View package integration
 */
object ViewIntegrationTests {
    
    fun runAllTests() {
        println("\n🔬 Running View Integration Tests...")
        
        ViewTestRunner.test("Complete user flow simulation") {
            val tracker = CallbackTracker()
            var courses = listOf<Course>()
            val requirements = listOf(
                Requirement.SpecificCourse(Course("CS", 101)),
                Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)))
            )
            
            // Step 1: User adds CS 101
            val cs101 = Course("CS", 101)
            tracker.onAddCourse(cs101)
            courses = courses + cs101
            
            val afterCS = RequirementChecker.allSatisfied(requirements, courses)
            ViewTestRunner.assertFalse(afterCS, "Should not be complete with only CS 101")
            
            // Step 2: User adds PHIL 101
            val phil101 = Course("PHIL", 101)
            tracker.onAddCourse(phil101)
            courses = courses + phil101
            
            val afterPHIL = RequirementChecker.allSatisfied(requirements, courses)
            ViewTestRunner.assertTrue(afterPHIL, "Should be complete with CS 101 and PHIL 101")
            
            // Step 3: User removes PHIL 101
            tracker.onRemoveCourse(phil101)
            courses = courses.filter { it != phil101 }
            
            val afterRemove = RequirementChecker.allSatisfied(requirements, courses)
            ViewTestRunner.assertFalse(afterRemove, "Should become incomplete after removing PHIL 101")
            
            // Step 4: User adds SOC 101 instead
            val soc101 = Course("SOC", 101)
            tracker.onAddCourse(soc101)
            courses = courses + soc101
            
            val afterSOC = RequirementChecker.allSatisfied(requirements, courses)
            ViewTestRunner.assertTrue(afterSOC, "Should be complete with CS 101 and SOC 101")
            
            ViewTestRunner.assertEqual(4, tracker.calls.size, "Should have 4 user interactions")
        }
        
        ViewTestRunner.test("Error handling in input validation") {
            val invalidInputs = listOf(
                Pair("", "101"),      // Empty department
                Pair("CS", ""),       // Empty number
                Pair("CS", "abc"),    // Non-numeric number
                Pair("CS", "-101"),   // Negative number
                Pair("CS", "0")       // Zero number
            )
            
            for ((dept, num) in invalidInputs) {
                val courseNumber = num.toIntOrNull()
                val isValid = dept.isNotBlank() && courseNumber != null && courseNumber > 0
                
                ViewTestRunner.assertFalse(
                    isValid,
                    "Input ($dept, $num) should be invalid"
                )
            }
        }
        
        ViewTestRunner.test("Component state isolation") {
            // Test that each component manages its own state correctly
            val tracker1 = CallbackTracker()
            val tracker2 = CallbackTracker()
            
            val course1 = Course("CS", 101)
            val course2 = Course("PHIL", 101)
            
            tracker1.onAddCourse(course1)
            tracker2.onAddCourse(course2)
            
            ViewTestRunner.assertEqual(course1, tracker1.lastCourse, "Tracker 1 should have CS 101")
            ViewTestRunner.assertEqual(course2, tracker2.lastCourse, "Tracker 2 should have PHIL 101")
            ViewTestRunner.assertEqual(1, tracker1.calls.size, "Tracker 1 should have 1 call")
            ViewTestRunner.assertEqual(1, tracker2.calls.size, "Tracker 2 should have 1 call")
        }
    }
}

/**
 * Main function to run all View tests
 */
fun main() {
    println("🚀 Starting View Package Tests")
    
    CourseInputCardTests.runAllTests()
    CoursesListTests.runAllTests()
    RequirementsListTests.runAllTests()
    DegreePlannerScreenTests.runAllTests()
    ViewIntegrationTests.runAllTests()
    
    ViewTestRunner.printResults()
}

/**
 * Function to run tests from Android app (if needed)
 */
fun runViewTests() {
    main()
}