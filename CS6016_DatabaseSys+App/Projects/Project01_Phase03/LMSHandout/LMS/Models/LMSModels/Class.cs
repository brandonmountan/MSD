using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels;

public partial class Class
{
    public int ClassId { get; set; }

    public uint SemesterYear { get; set; }

    public string SemesterSeason { get; set; } = null!;

    public string Location { get; set; } = null!;

    public TimeOnly StartTime { get; set; }

    public TimeOnly EndTime { get; set; }

    public int CourseId { get; set; }

    public string ProfessorUId { get; set; } = null!;

    public virtual ICollection<AssignmentCategory> AssignmentCategories { get; set; } = new List<AssignmentCategory>();

    public virtual Course Course { get; set; } = null!;

    public virtual ICollection<Enrollment> Enrollments { get; set; } = new List<Enrollment>();

    public virtual Professor ProfessorU { get; set; } = null!;
}
