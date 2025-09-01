package com.example.degreeplanner

import com.example.degreeplanner.model.Course
import com.example.degreeplanner.model.Requirement
import com.example.degreeplanner.model.RequirementChecker
import com.example.degreeplanner.model.runModelTests
import com.example.degreeplanner.viewmodel.runViewModelTests
import com.example.degreeplanner.view.runViewTests
import com.example.degreeplanner.viewmodel.DegreePlannerViewModel

/**
 * Custom Test Suite Runner that runs all package tests
 * This replaces JUnit for running comprehensive tests
 */
object TestSuiteRunner {
    
    fun runAllTests() {
        println("🎯 DEGREE PLANNER - COMPLETE TEST SUITE")
        println("=" * 60)
        println("Running custom tests without JUnit framework")
        println("Testing all packages: Model, ViewModel, View")
        println("=" * 60)
        
        val startTime = System.currentTimeMillis()
        
        try {
            // Run Model package tests
            println("\n📦 TESTING MODEL PACKAGE")
            println("-" * 40)
            runModelTests()
            
            // Run ViewModel package tests  
            println("\n📦 TESTING VIEWMODEL PACKAGE")
            println("-" * 40)
            runViewModelTests()
            
            // Run View package tests
            println("\n📦 TESTING VIEW PACKAGE")
            println("-" * 40)
            runViewTests()
            
        } catch (e: Exception) {
            println("\n💥 CRITICAL ERROR: ${e.message}")
            e.printStackTrace()
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        printFinalSummary(duration)
    }
    
    private fun printFinalSummary(duration: Long) {
        println("\n" + "=" * 60)
        println("🏁 FINAL TEST SUITE SUMMARY")
        println("=" * 60)
        println("⏱️  Duration: ${duration}ms")
        println("📦 Packages tested: 3 (Model, ViewModel, View)")
        println("🧪 Test categories covered:")
        println("   • Data class behavior")
        println("   • Business logic correctness")
        println("   • State management")
        println("   • Component interaction")
        println("   • User flow simulation")
        println("   • Edge cases and error handling")
        println("\n✨ Custom testing framework used (no JUnit dependency)")
        println("=" * 60)
    }
}

/**
 * Individual package test runners for targeted testing
 */
object IndividualTestRunner {
    
    fun runModelTestsOnly() {
        println("🧪 Running ONLY Model Package Tests")
        runModelTests()
    }
    
    fun runViewModelTestsOnly() {
        println("🧪 Running ONLY ViewModel Package Tests")
        runViewModelTests()
    }
    
    fun runViewTestsOnly() {
        println("🧪 Running ONLY View Package Tests")
        runViewTests()
    }
}

/**
 * Smoke test runner - quick tests to verify basic functionality
 */
object SmokeTestRunner {
    
    fun runSmokeTests() {
        println("🔥 Running Smoke Tests (Quick Verification)")
        println("-" * 50)
        
        var passCount = 0
        var failCount = 0
        
        // Quick test of basic functionality
        try {
            // Test Course creation
            val course = Course("CS", 101)
            assert(course.toString() == "CS 101") { "Course toString failed" }
            passCount++
            println("✅ Course creation works")
            
            // Test Requirement checking
            val requirement = Requirement.SpecificCourse(course)
            val courses = listOf(course)
            assert(RequirementChecker.isSatisfied(requirement, courses)) { "Requirement checking failed" }
            passCount++
            println("✅ Requirement checking works")
            
            // Test ViewModel basic functionality
            val viewModel = DegreePlannerViewModel()
            viewModel.addCourse(course)
            assert(viewModel.courses.contains(course)) { "ViewModel add course failed" }
            passCount++
            println("✅ ViewModel functionality works")
            
        } catch (e: Exception) {
            failCount++
            println("❌ Smoke test failed: ${e.message}")
        }
        
        println("\n🔥 Smoke Test Results: $passCount passed, $failCount failed")
        if (failCount == 0) {
            println("✅ All core functionality working!")
        }
    }
}

/**
 * Performance test runner - tests for performance characteristics
 */
object PerformanceTestRunner {
    
    fun runPerformanceTests() {
        println("⚡ Running Performance Tests")
        println("-" * 40)
        
        // Test 1: Large course list performance
        val startTime1 = System.currentTimeMillis()
        val largeCourseList = (1..1000).map { Course("DEPT$it", it) }
        val requirement = Requirement.OneOf(largeCourseList)
        val testCourses = listOf(Course("DEPT500", 500))
        val result = RequirementChecker.isSatisfied(requirement, testCourses)
        val duration1 = System.currentTimeMillis() - startTime1
        
        println("✅ Large course list test (1000 courses): ${duration1}ms")
        assert(result) { "Should find course in large list" }
        
        // Test 2: Many requirements performance
        val startTime2 = System.currentTimeMillis()
        val manyRequirements = (1..100).map { 
            Requirement.SpecificCourse(Course("CS", it))
        }
        val manyCourses = (1..100).map { Course("CS", it) }
        val allSatisfied = RequirementChecker.allSatisfied(manyRequirements, manyCourses)
        val duration2 = System.currentTimeMillis() - startTime2
        
        println("✅ Many requirements test (100 req): ${duration2}ms")
        assert(allSatisfied) { "Should satisfy all requirements" }
        
        // Test 3: ViewModel operations performance
        val startTime3 = System.currentTimeMillis()
        val viewModel = DegreePlannerViewModel()
        repeat(1000) {
            viewModel.addCourse(Course("TEST$it", it))
        }
        val duration3 = System.currentTimeMillis() - startTime3
        
        println("✅ ViewModel operations (1000 adds): ${duration3}ms")
        
        println("\n⚡ Performance tests completed successfully!")
    }
}

/**
 * Main entry point - run this to test everything
 */
fun main() {
    // You can choose what to run:
    
    // Option 1: Run everything
    TestSuiteRunner.runAllTests()
    
    // Option 2: Run quick smoke test first
    // SmokeTestRunner.runSmokeTests()
    
    // Option 3: Run performance tests
    // PerformanceTestRunner.runPerformanceTests()
    
    // Option 4: Run individual packages
    // IndividualTestRunner.runModelTestsOnly()
    // IndividualTestRunner.runViewModelTestsOnly()
    // IndividualTestRunner.runViewTestsOnly()
}

/**
 * Helper functions for integration with Android app
 */
object AndroidTestIntegration {
    
    /**
     * Call this from your MainActivity or a test button to run tests
     */
    fun runTestsFromApp() {
        println("🤖 Running tests from Android app...")
        TestSuiteRunner.runAllTests()
    }
    
    /**
     * Quick test that can be called from app startup for debugging
     */
    fun runQuickTest(): Boolean {
        return try {
            val course = Course("CS", 101)
            val requirement = Requirement.SpecificCourse(course)
            RequirementChecker.isSatisfied(requirement, listOf(course))
        } catch (e: Exception) {
            false
        }
    }
}

// Extension function for string multiplication (like Python)
private operator fun String.times(n: Int): String = this.repeat(n)