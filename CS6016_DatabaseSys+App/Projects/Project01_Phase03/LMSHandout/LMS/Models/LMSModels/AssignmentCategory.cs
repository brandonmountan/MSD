using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels;

public partial class AssignmentCategory
{
    public int CategoryId { get; set; }

    public string Name { get; set; } = null!;

    public uint GradeWeight { get; set; }

    public int ClassId { get; set; }

    public virtual ICollection<Assignment> Assignments { get; set; } = new List<Assignment>();

    public virtual Class Class { get; set; } = null!;
}
