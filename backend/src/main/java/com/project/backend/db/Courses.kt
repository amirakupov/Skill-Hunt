package com.project.backend.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Courses : LongIdTable("courses") {
    val userEmail    = varchar("user_email", 255).index()
    val title        = varchar("title", 255)
    val category     = varchar("category", 100)
    val description  = text("description")
    val skillLevel   = varchar("skill_level", 50)
    val locationType = varchar("location_type", 20)  // "meet-up" or "online"
    val availability = varchar("availability", 255)
    val contactInfo  = varchar("contact_info", 255)
    val createdAt    = datetime("created_at")
}
