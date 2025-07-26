using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels;

public partial class Submission
{
    public string StudentUId { get; set; } = null!;

    public int AssignmentId { get; set; }

    public DateTime SubmissionDatetime { get; set; }

    public uint? Score { get; set; }

    public string? Contents { get; set; }

    public virtual Assignment Assignment { get; set; } = null!;

    public virtual Student StudentU { get; set; } = null!;
}
