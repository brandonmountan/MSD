// SimpleUITests.kt - SIMPLE Compose UI tests
package com.example.degreeplanner

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.degreeplanner.model.*
import com.example.degreeplanner.view.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SimpleUITests {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun courseInputCard_canAddCourse() {
        var addedCourse: Course? = null
        
        composeTestRule.setContent {
            CourseInputCard(onAddCourse = { addedCourse = it })
        }
        
        // Type in course info
        composeTestRule.onNodeWithText("Department").performTextInput("CS")
        composeTestRule.onNodeWithText("Number").performTextInput("101")
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        
        // Check course was added
        assert(addedCourse?.department == "CS")
        assert(addedCourse?.number == 101)
    }
    
    @Test
    fun coursesList_showsCourses() {
        val courses = listOf(Course("CS", 101), Course("PHIL", 101))
        
        composeTestRule.setContent {
            CoursesList(courses = courses, onRemove = {})
        }
        
        // Check courses are displayed
        composeTestRule.onNodeWithText("CS 101").assertIsDisplayed()
        composeTestRule.onNodeWithText("PHIL 101").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Courses (2)").assertIsDisplayed()
    }
    
    @Test
    fun coursesList_emptyShowsMessage() {
        composeTestRule.setContent {
            CoursesList(courses = emptyList(), onRemove = {})
        }
        
        composeTestRule.onNodeWithText("No courses added yet").assertIsDisplayed()
    }
    
    @Test
    fun requirementsList_showsStatus() {
        val requirements = listOf(
            Requirement.SpecificCourse(Course("CS", 101), "Take CS 101")
        )
        val courses = listOf(Course("CS", 101))
        
        composeTestRule.setContent {
            RequirementsList(requirements = requirements, courses = courses)
        }
        
        composeTestRule.onNodeWithText("Take CS 101").assertIsDisplayed()
        composeTestRule.onNodeWithText("✅").assertIsDisplayed()
        composeTestRule.onNodeWithText("🎉 Degree requirements completed!").assertIsDisplayed()
    }
    
    @Test
    fun planSelector_showsPlans() {
        val plans = listOf(
            PlanInfo("Computer Science", "cs.json"),
            PlanInfo("English", "english.json")
        )
        
        composeTestRule.setContent {
            PlanSelector(
                plans = plans,
                selectedPlan = null,
                isLoading = false,
                onPlanSelected = {},
                onRefresh = {}
            )
        }
        
        composeTestRule.onNodeWithText("Computer Science").assertIsDisplayed()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
    }
    
    @Test
    fun planSelector_loadingShowsMessage() {
        composeTestRule.setContent {
            PlanSelector(
                plans = emptyList(),
                selectedPlan = null,
                isLoading = true,
                onPlanSelected = {},
                onRefresh = {}
            )
        }
        
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }
    
    @Test
    fun mainScreen_showsAllComponents() {
        composeTestRule.setContent {
            DegreePlannerScreen()
        }
        
        // Check main components exist
        composeTestRule.onNodeWithText("Simple Degree Planner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Degree Plans").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Course").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Courses (0)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Degree Requirements").assertIsDisplayed()
    }
}