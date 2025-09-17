// SimplePhase2Tests.kt - CORRECTED for actual API structure
package com.example.degreeplanner

import com.example.degreeplanner.model.*
import com.example.degreeplanner.viewmodel.DegreePlannerViewModel
import org.junit.Test
import org.junit.Assert.*

/**
 * PHASE 2: JUnit tests - CORRECTED for actual API structure
 */
class SimplePhase2Tests {
    
    // ===== BUSINESS LOGIC TESTS =====
    
    @Test
    fun `requirement checker works with CS 101`() {
        val requirement = Requirement.SpecificCourse(Course("CS", 101))
        val courses = listOf(Course("CS", 101))
        
        assertTrue(RequirementChecker.isSatisfied(requirement, courses))
    }
    
    @Test
    fun `requirement checker works with choice requirement`() {
        val requirement = Requirement.OneOf(
            listOf(Course("PHIL", 101), Course("SOC", 101)),
            "Philosophy or Sociology"
        )
        val courses = listOf(Course("PHIL", 101))
        
        assertTrue(RequirementChecker.isSatisfied(requirement, courses))
    }
    
    @Test
    fun `all requirements satisfied when both complete`() {
        val requirements = listOf(
            Requirement.SpecificCourse(Course("CS", 101)),
            Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)), "Choice")
        )
        val courses = listOf(Course("CS", 101), Course("PHIL", 101))
        
        assertTrue(RequirementChecker.allSatisfied(requirements, courses))
    }
    
    // ===== VIEWMODEL TESTS =====
    
    @Test
    fun `viewModel addCourse works`() {
        val viewModel = DegreePlannerViewModel()
        val course = Course("CS", 101)
        
        viewModel.addCourse(course)
        
        assertTrue(viewModel.courses.contains(course))
    }
    
    @Test
    fun `viewModel prevents duplicates`() {
        val viewModel = DegreePlannerViewModel()
        val course = Course("CS", 101)
        
        viewModel.addCourse(course)
        viewModel.addCourse(course) // Add same course again
        
        assertEquals(1, viewModel.courses.size)
    }
    
    @Test
    fun `viewModel removeCourse works`() {
        val viewModel = DegreePlannerViewModel()
        val course = Course("CS", 101)
        
        viewModel.addCourse(course)
        viewModel.removeCourse(course)
        
        assertFalse(viewModel.courses.contains(course))
    }
    
    @Test
    fun `viewModel isComplete works with default requirements`() {
        val viewModel = DegreePlannerViewModel()
        
        // Not complete initially
        assertFalse(viewModel.isComplete())
        
        // Add courses to complete default requirements
        viewModel.addCourse(Course("CS", 101))
        viewModel.addCourse(Course("PHIL", 101))
        
        // Should be complete now
        assertTrue(viewModel.isComplete())
    }
    
    // ===== API MODEL CONVERSION TESTS (CORRECTED) =====
    
    @Test
    fun `ApiCourse converts to domain Course correctly`() {
        // Test with string number (actual API format)
        val apiCourse = ApiCourse("CS", "101")
        val domainCourse = apiCourse.toCourse()
        
        assertEquals("CS", domainCourse.department)
        assertEquals(101, domainCourse.number)
    }
    
    @Test
    fun `ApiCourse handles string numbers like 2001`() {
        // Test with higher course number as string
        val apiCourse = ApiCourse("EN", "2001")
        val domainCourse = apiCourse.toCourse()
        
        assertEquals("EN", domainCourse.department)
        assertEquals(2001, domainCourse.number)
    }
    
    @Test
    fun `ApiRequirement converts requiredCourse type correctly`() {
        val apiRequirement = ApiRequirement(
            type = "requiredCourse",
            course = ApiCourse("CS", "101")
        )
        
        val domainRequirement = apiRequirement.toRequirement()
        
        assertNotNull(domainRequirement)
        assertTrue(domainRequirement is Requirement.SpecificCourse)
        assertEquals("Complete CS 101", domainRequirement!!.description)
    }
    
    @Test
    fun `ApiRequirement converts oneOf type correctly`() {
        val apiRequirement = ApiRequirement(
            type = "oneOf",
            courses = listOf(
                ApiCourse("EN", "2001"),
                ApiCourse("EN", "2002")
            )
        )
        
        val domainRequirement = apiRequirement.toRequirement()
        
        assertNotNull(domainRequirement)
        assertTrue(domainRequirement is Requirement.OneOf)
        assertEquals("Complete one of: EN 2001 or EN 2002", domainRequirement!!.description)
    }
    
    @Test
    fun `ApiRequirement handles unknown type gracefully`() {
        val apiRequirement = ApiRequirement(
            type = "unknownType"
        )
        
        val domainRequirement = apiRequirement.toRequirement()
        
        // Should return null for unknown types
        assertNull(domainRequirement)
    }
    
    @Test
    fun `PlanDetails converts to domain requirements correctly`() {
        val planDetails = PlanDetails(
            name = "Computer Science",
            requirements = listOf(
                ApiRequirement(
                    type = "requiredCourse",
                    course = ApiCourse("CS", "101")
                ),
                ApiRequirement(
                    type = "oneOf",
                    courses = listOf(
                        ApiCourse("CS", "2000"),
                        ApiCourse("CS", "2001")
                    )
                )
            )
        )
        
        val domainRequirements = planDetails.toRequirements()
        
        assertEquals(2, domainRequirements.size)
        assertTrue(domainRequirements[0] is Requirement.SpecificCourse)
        assertTrue(domainRequirements[1] is Requirement.OneOf)
    }
    
    // ===== INTEGRATION TESTS =====
    
    @Test
    fun `complete user workflow with English degree requirements`() {
        val viewModel = DegreePlannerViewModel()
        
        // Simulate loading English degree requirements
        val englishRequirements = listOf(
            Requirement.SpecificCourse(Course("EN", 101), "Complete EN 101"),
            Requirement.OneOf(
                listOf(Course("EN", 2001), Course("EN", 2002)),
                "Complete one of: EN 2001 or EN 2002"
            )
        )
        
        // Initially not complete
        assertFalse(viewModel.isComplete())
        
        // Add EN 101
        viewModel.addCourse(Course("EN", 101))
        // Still not complete (missing choice requirement)
        
        // Add EN 2001 (satisfies choice requirement)
        viewModel.addCourse(Course("EN", 2001))
        
        // Verify courses were added
        assertEquals(2, viewModel.courses.size)
        assertTrue(viewModel.courses.contains(Course("EN", 101)))
        assertTrue(viewModel.courses.contains(Course("EN", 2001)))
    }
    
    @Test
    fun `computer science degree workflow`() {
        val viewModel = DegreePlannerViewModel()
        
        // Add CS courses for Computer Science degree
        viewModel.addCourse(Course("CS", 101))  // Required
        viewModel.addCourse(Course("CS", 102))  // Required
        viewModel.addCourse(Course("CS", 2000)) // Choice (2000 or 2001)
        
        // Verify all courses added
        assertEquals(3, viewModel.courses.size)
        assertTrue(viewModel.courses.contains(Course("CS", 101)))
        assertTrue(viewModel.courses.contains(Course("CS", 102)))
        assertTrue(viewModel.courses.contains(Course("CS", 2000)))
    }
}