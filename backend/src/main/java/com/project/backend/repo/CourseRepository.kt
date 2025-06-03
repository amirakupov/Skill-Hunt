package com.project.backend.repo

import com.project.backend.models.AddCourseRequest
import com.project.backend.models.CourseResponse
import com.project.backend.db.Courses
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

interface ICourseRepository {
    fun addCourse(req: AddCourseRequest, userEmail: String): CourseResponse
    fun getCoursesByUser(userEmail: String): List<CourseResponse>
    fun getAllCourses(): List<CourseResponse>
}

object CourseRepositoryImpl : ICourseRepository {
    override fun addCourse(req: AddCourseRequest, userEmail: String) = transaction {
        val now = org.joda.time.DateTime.now()
        val id = Courses.insertAndGetId {
            it[Courses.userEmail]    = userEmail
            it[Courses.title]        = req.title
            it[Courses.category]     = req.category
            it[Courses.description]  = req.description
            it[Courses.skillLevel]   = req.skillLevel
            it[Courses.locationType] = req.locationType
            it[Courses.availability] = req.availability
            it[Courses.contactInfo]  = req.contactInfo
            it[Courses.createdAt]    = now
        }.value

        CourseResponse(
            id           = id,
            userEmail    = userEmail,
            title        = req.title,
            category     = req.category,
            description  = req.description,
            skillLevel   = req.skillLevel,
            locationType = req.locationType,
            availability = req.availability,
            contactInfo  = req.contactInfo,
            createdAt    = now.toString()
        )
    }
    override fun getCoursesByUser(userEmail: String) = transaction {
        Courses
            .select { Courses.userEmail eq userEmail }
            .orderBy(Courses.createdAt, SortOrder.DESC)
            .map {
                CourseResponse(
                    id           = it[Courses.id].value,
                    userEmail    = it[Courses.userEmail],
                    title        = it[Courses.title],
                    category     = it[Courses.category],
                    description  = it[Courses.description],
                    skillLevel   = it[Courses.skillLevel],
                    locationType = it[Courses.locationType],
                    availability = it[Courses.availability],
                    contactInfo  = it[Courses.contactInfo],
                    createdAt    = it[Courses.createdAt].toString()
                )
            }
    }
    override fun getAllCourses(): List<CourseResponse> = transaction {
        Courses
            .selectAll()
            .orderBy(Courses.createdAt, SortOrder.DESC)
            .map {
                CourseResponse(
                    id           = it[Courses.id].value,
                    userEmail    = it[Courses.userEmail],
                    title        = it[Courses.title],
                    category     = it[Courses.category],
                    description  = it[Courses.description],
                    skillLevel   = it[Courses.skillLevel],
                    locationType = it[Courses.locationType],
                    availability = it[Courses.availability],
                    contactInfo  = it[Courses.contactInfo],
                    createdAt    = it[Courses.createdAt].toString()
                )
            }
    }
}
