You have TWO choices for chart templates.  In both cases, the [TemplateReplacements](TemplateReplacements.md) occur prior to processing.

## [Pentaho XML](XMLTemplates.md) (default) ##

Reads a Pentaho chart XML document, processes the tags/chart types supported, and outputs a chart that would be similar to the one output by the Pentaho standard chart component.

## PentahoFlashChart XML ##

Uses a set of XML defined for this project.  In many cases this chart format is more powerful because it allows all kinds of OFC type extensions.  To use this format, set the variable "use\_pentaho\_xml" variable to "false"