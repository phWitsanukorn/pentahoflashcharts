The component reads the template (PFC XML format or Pentaho XML format), and then replaces all occurences of the below tokens with the data from the "chart\_dataset" that was an input to the component.

## ${colN} ##

${colN} will be replaced with a comma separated list of values of the column in N in the dataset
For instance, ${col1} could be replaced with the values in the FIRST column of the data set and end up looking like2039,193,8930,3839,1023

## ${minN} ##
${minN} will be replaced with the minimum value in the column N in the dataset.

`<range-minimum>${min2}</range-minimum>` could be replaced with minimum values in SECOND column of the data set

## ${maxN} ##
${maxN} will be replaced with the maximum value in the column N in the dataset
`<range-title>Sales up to ${max1}</range-title>` could be replaced with `<range-title>Sales up to 9,828</range-title>`

## ${headN} ##
${headN} will be replaced with the “header value” for the column N in the dataset
`<domain-title>${head1}</domain-title>` could be replaced by `<domain-title>Region</domain-title>`

This will probably match your JDBC header if you are using SQL (`select col1 as "Region" from ...`)

## ${customVariable} ##
Any custom variable that was passed to the component can be included.  This is useful if you want to create your Title programatically ahead of time.

`<title>${mypassedintitlevar}</title>` could be replaced with `<title>Sales for December 2007 thru April 2008</title>`