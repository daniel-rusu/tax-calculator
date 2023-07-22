# Tax Calculator

A collection of algorithms for computing income tax. These algorithms extend to other domains such as range searching,
classification, or even collision detection of static entities.

## Problem Summary

Most regions of the world have an incremental income tax system where the tax rate starts low and increases for each
subsequent chunk of income.

For example, here are the effective tax rates in the state of Texas for 2022:

| Tax Rate | For Income                |
|:--------:|:--------------------------|
|   10%    | from $0 to $10,275        |
|   12%    | from $10,275 to $41,775   |
|   22%    | from $41,775 to $89,075   |
|   24%    | from $89,075 to $170,050  |
|   32%    | from $170,050 to $215,950 |
|   35%    | from $215,950 to $539,900 |
|   37%    | over $539,900             |

Given the above tax brackets, if you lived in Texas in 2022 and had a taxable income of $50,000, then your income tax
would be calculated as follows:

|        Tax Amount | Reasoning                                             |
|------------------:|:------------------------------------------------------|
|   $1,027.50 (10%) | for the first $10,275                                 |
| + $3,780.00 (12%) | for the $31,500 chunk between $10,275 and $41,775     |
| + $1,809.50 (22%) | for the last $8,225 chunk between $41,775 and $50,000 | 
| =       $6,617.00 |                                                       | 

## Requirements

Create an algorithm that calculates an individual’s income tax given their taxable income. A large number of tax
brackets, containing the tax percentage associated with each income range, will be provided in the structure of your
choice. This algorithm will be used many times to compute the tax for each person in a large population.

Create a `TaxCalculator` class that:

* Has a constructor which accepts the tax brackets
* Performs any one-time setup during initialization
* Has a `computeTax` function that accepts an individual's taxable income and returns the computed income tax
