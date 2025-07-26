using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Pomelo.EntityFrameworkCore.MySql.Scaffolding.Internal;

namespace LMS.Models.LMSModels;

public partial class LMSContext : DbContext
{
    public LMSContext()
    {
    }

    public LMSContext(DbContextOptions<LMSContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Administrator> Administrators { get; set; }

    public virtual DbSet<Assignment> Assignments { get; set; }

    public virtual DbSet<AssignmentCategory> AssignmentCategories { get; set; }

    public virtual DbSet<Class> Classes { get; set; }

    public virtual DbSet<Course> Courses { get; set; }

    public virtual DbSet<Department> Departments { get; set; }

    public virtual DbSet<Enrollment> Enrollments { get; set; }

    public virtual DbSet<Professor> Professors { get; set; }

    public virtual DbSet<Student> Students { get; set; }

    public virtual DbSet<Submission> Submissions { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseMySql("name=LMS:LMSConnectionString", Microsoft.EntityFrameworkCore.ServerVersion.Parse("10.11.8-mariadb"));

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder
            .UseCollation("latin1_swedish_ci")
            .HasCharSet("latin1");

        modelBuilder.Entity<Administrator>(entity =>
        {
            entity.HasKey(e => e.UId).HasName("PRIMARY");

            entity.Property(e => e.UId)
                .HasMaxLength(8)
                .HasColumnName("uID");
            entity.Property(e => e.DateOfBirth).HasColumnName("date_of_birth");
            entity.Property(e => e.FirstName)
                .HasMaxLength(100)
                .HasColumnName("first_name");
            entity.Property(e => e.LastName)
                .HasMaxLength(100)
                .HasColumnName("last_name");
        });

        modelBuilder.Entity<Assignment>(entity =>
        {
            entity.HasKey(e => e.AssignmentId).HasName("PRIMARY");

            entity.HasIndex(e => e.CategoryId, "category_id");

            entity.HasIndex(e => new { e.Name, e.CategoryId }, "unique_assignment_per_category").IsUnique();

            entity.Property(e => e.AssignmentId)
                .HasColumnType("int(11)")
                .HasColumnName("assignment_id");
            entity.Property(e => e.CategoryId)
                .HasColumnType("int(11)")
                .HasColumnName("category_id");
            entity.Property(e => e.Contents)
                .HasColumnType("text")
                .HasColumnName("contents");
            entity.Property(e => e.DueDatetime)
                .HasColumnType("datetime")
                .HasColumnName("due_datetime");
            entity.Property(e => e.MaxPoints)
                .HasColumnType("int(10) unsigned")
                .HasColumnName("max_points");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");

            entity.HasOne(d => d.Category).WithMany(p => p.Assignments)
                .HasForeignKey(d => d.CategoryId)
                .HasConstraintName("Assignments_ibfk_1");
        });

        modelBuilder.Entity<AssignmentCategory>(entity =>
        {
            entity.HasKey(e => e.CategoryId).HasName("PRIMARY");

            entity.ToTable("Assignment_Categories");

            entity.HasIndex(e => e.ClassId, "class_id");

            entity.HasIndex(e => new { e.Name, e.ClassId }, "unique_category_per_class").IsUnique();

            entity.Property(e => e.CategoryId)
                .HasColumnType("int(11)")
                .HasColumnName("category_id");
            entity.Property(e => e.ClassId)
                .HasColumnType("int(11)")
                .HasColumnName("class_id");
            entity.Property(e => e.GradeWeight)
                .HasColumnType("int(10) unsigned")
                .HasColumnName("grade_weight");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");

            entity.HasOne(d => d.Class).WithMany(p => p.AssignmentCategories)
                .HasForeignKey(d => d.ClassId)
                .HasConstraintName("Assignment_Categories_ibfk_1");
        });

        modelBuilder.Entity<Class>(entity =>
        {
            entity.HasKey(e => e.ClassId).HasName("PRIMARY");

            entity.HasIndex(e => e.CourseId, "course_id");

            entity.HasIndex(e => e.ProfessorUId, "professor_uID");

            entity.HasIndex(e => new { e.SemesterYear, e.SemesterSeason, e.CourseId }, "unique_class_per_semester").IsUnique();

            entity.Property(e => e.ClassId)
                .HasColumnType("int(11)")
                .HasColumnName("class_id");
            entity.Property(e => e.CourseId)
                .HasColumnType("int(11)")
                .HasColumnName("course_id");
            entity.Property(e => e.EndTime)
                .HasColumnType("time")
                .HasColumnName("end_time");
            entity.Property(e => e.Location)
                .HasMaxLength(100)
                .HasColumnName("location");
            entity.Property(e => e.ProfessorUId)
                .HasMaxLength(8)
                .HasColumnName("professor_uID");
            entity.Property(e => e.SemesterSeason)
                .HasColumnType("enum('Spring','Fall','Summer')")
                .HasColumnName("semester_season");
            entity.Property(e => e.SemesterYear)
                .HasColumnType("int(10) unsigned")
                .HasColumnName("semester_year");
            entity.Property(e => e.StartTime)
                .HasColumnType("time")
                .HasColumnName("start_time");

            entity.HasOne(d => d.Course).WithMany(p => p.Classes)
                .HasForeignKey(d => d.CourseId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("Classes_ibfk_1");

            entity.HasOne(d => d.ProfessorU).WithMany(p => p.Classes)
                .HasForeignKey(d => d.ProfessorUId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("Classes_ibfk_2");
        });

        modelBuilder.Entity<Course>(entity =>
        {
            entity.HasKey(e => e.CourseId).HasName("PRIMARY");

            entity.HasIndex(e => e.Department, "department");

            entity.HasIndex(e => new { e.Number, e.Department }, "unique_course_per_dept").IsUnique();

            entity.Property(e => e.CourseId)
                .HasColumnType("int(11)")
                .HasColumnName("course_id");
            entity.Property(e => e.Department)
                .HasMaxLength(4)
                .HasColumnName("department");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");
            entity.Property(e => e.Number)
                .HasColumnType("int(11)")
                .HasColumnName("number");

            entity.HasOne(d => d.DepartmentNavigation).WithMany(p => p.Courses)
                .HasForeignKey(d => d.Department)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("Courses_ibfk_1");
        });

        modelBuilder.Entity<Department>(entity =>
        {
            entity.HasKey(e => e.SubjectAbbrev).HasName("PRIMARY");

            entity.Property(e => e.SubjectAbbrev)
                .HasMaxLength(4)
                .HasColumnName("subject_abbrev");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");
        });

        modelBuilder.Entity<Enrollment>(entity =>
        {
            entity.HasKey(e => new { e.StudentUId, e.ClassId })
                .HasName("PRIMARY")
                .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0 });

            entity.ToTable("Enrollment");

            entity.HasIndex(e => e.ClassId, "class_id");

            entity.Property(e => e.StudentUId)
                .HasMaxLength(8)
                .HasColumnName("student_uID");
            entity.Property(e => e.ClassId)
                .HasColumnType("int(11)")
                .HasColumnName("class_id");
            entity.Property(e => e.Grade)
                .HasMaxLength(2)
                .HasColumnName("grade");

            entity.HasOne(d => d.Class).WithMany(p => p.Enrollments)
                .HasForeignKey(d => d.ClassId)
                .HasConstraintName("Enrollment_ibfk_2");

            entity.HasOne(d => d.StudentU).WithMany(p => p.Enrollments)
                .HasForeignKey(d => d.StudentUId)
                .HasConstraintName("Enrollment_ibfk_1");
        });

        modelBuilder.Entity<Professor>(entity =>
        {
            entity.HasKey(e => e.UId).HasName("PRIMARY");

            entity.HasIndex(e => e.WorksInDept, "works_in_dept");

            entity.Property(e => e.UId)
                .HasMaxLength(8)
                .HasColumnName("uID");
            entity.Property(e => e.DateOfBirth).HasColumnName("date_of_birth");
            entity.Property(e => e.FirstName)
                .HasMaxLength(100)
                .HasColumnName("first_name");
            entity.Property(e => e.LastName)
                .HasMaxLength(100)
                .HasColumnName("last_name");
            entity.Property(e => e.WorksInDept)
                .HasMaxLength(4)
                .HasColumnName("works_in_dept");

            entity.HasOne(d => d.WorksInDeptNavigation).WithMany(p => p.Professors)
                .HasForeignKey(d => d.WorksInDept)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("Professors_ibfk_1");
        });

        modelBuilder.Entity<Student>(entity =>
        {
            entity.HasKey(e => e.UId).HasName("PRIMARY");

            entity.HasIndex(e => e.MajorDept, "major_dept");

            entity.Property(e => e.UId)
                .HasMaxLength(8)
                .HasColumnName("uID");
            entity.Property(e => e.DateOfBirth).HasColumnName("date_of_birth");
            entity.Property(e => e.FirstName)
                .HasMaxLength(100)
                .HasColumnName("first_name");
            entity.Property(e => e.LastName)
                .HasMaxLength(100)
                .HasColumnName("last_name");
            entity.Property(e => e.MajorDept)
                .HasMaxLength(4)
                .HasColumnName("major_dept");

            entity.HasOne(d => d.MajorDeptNavigation).WithMany(p => p.Students)
                .HasForeignKey(d => d.MajorDept)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("Students_ibfk_1");
        });

        modelBuilder.Entity<Submission>(entity =>
        {
            entity.HasKey(e => new { e.StudentUId, e.AssignmentId })
                .HasName("PRIMARY")
                .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0 });

            entity.ToTable("Submission");

            entity.HasIndex(e => e.AssignmentId, "assignment_id");

            entity.Property(e => e.StudentUId)
                .HasMaxLength(8)
                .HasColumnName("student_uID");
            entity.Property(e => e.AssignmentId)
                .HasColumnType("int(11)")
                .HasColumnName("assignment_id");
            entity.Property(e => e.Contents)
                .HasColumnType("text")
                .HasColumnName("contents");
            entity.Property(e => e.Score)
                .HasColumnType("int(10) unsigned")
                .HasColumnName("score");
            entity.Property(e => e.SubmissionDatetime)
                .HasColumnType("datetime")
                .HasColumnName("submission_datetime");

            entity.HasOne(d => d.Assignment).WithMany(p => p.Submissions)
                .HasForeignKey(d => d.AssignmentId)
                .HasConstraintName("Submission_ibfk_2");

            entity.HasOne(d => d.StudentU).WithMany(p => p.Submissions)
                .HasForeignKey(d => d.StudentUId)
                .HasConstraintName("Submission_ibfk_1");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
