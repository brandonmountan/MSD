using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels;

public partial class Professor
{
    public string UId { get; set; } = null!;

    public string FirstName { get; set; } = null!;

    public string LastName { get; set; } = null!;

    public DateOnly DateOfBirth { get; set; }

    public string WorksInDept { get; set; } = null!;

    public virtual ICollection<Class> Classes { get; set; } = new List<Class>();

    public virtual Department WorksInDeptNavigation { get; set; } = null!;
}
