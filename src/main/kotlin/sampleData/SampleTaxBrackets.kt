package sampleData

import dataModel.Money.Companion.dollars
import dataModel.Percent.Companion.basisPoints
import dataModel.Percent.Companion.percent
import dataModel.TaxBracket

object SampleTaxBrackets {
    /** Texas tax brackets for tax year 2022 */
    val texasTaxBrackets = listOf(
        TaxBracket(10.percent, from = 0.dollars, to = 10_275.dollars),
        TaxBracket(12.percent, from = 10_275.dollars, to = 41_775.dollars),
        TaxBracket(22.percent, from = 41_775.dollars, to = 89_075.dollars),
        TaxBracket(24.percent, from = 89_075.dollars, to = 170_050.dollars),
        TaxBracket(32.percent, from = 170_050.dollars, to = 215_950.dollars),
        TaxBracket(35.percent, from = 215_950.dollars, to = 539_900.dollars),
        TaxBracket(37.percent, from = 539_900.dollars),
    )

    /** Combined state and federal tax brackets for tax year 2022 */
    val hawaiiTaxBrackets = listOf(
        TaxBracket(11_40.basisPoints, from = 0.dollars, to = 2_400.dollars),
        TaxBracket(13_20.basisPoints, from = 2_400.dollars, to = 4_800.dollars),
        TaxBracket(15_50.basisPoints, from = 4_800.dollars, to = 9_600.dollars),
        TaxBracket(16_40.basisPoints, from = 9_600.dollars, to = 10_275.dollars),
        TaxBracket(18_40.basisPoints, from = 10_275.dollars, to = 14_400.dollars),
        TaxBracket(18_80.basisPoints, from = 14_400.dollars, to = 19_200.dollars),
        TaxBracket(19_20.basisPoints, from = 19_200.dollars, to = 24_000.dollars),
        TaxBracket(19_60.basisPoints, from = 24_000.dollars, to = 36_000.dollars),
        TaxBracket(19_90.basisPoints, from = 36_000.dollars, to = 41_775.dollars),
        TaxBracket(29_90.basisPoints, from = 41_775.dollars, to = 48_000.dollars),
        TaxBracket(30_25.basisPoints, from = 48_000.dollars, to = 89_075.dollars),
        TaxBracket(32_25.basisPoints, from = 89_075.dollars, to = 150_000.dollars),
        TaxBracket(33_00.basisPoints, from = 150_000.dollars, to = 170_050.dollars),
        TaxBracket(41_00.basisPoints, from = 170_050.dollars, to = 175_000.dollars),
        TaxBracket(42_00.basisPoints, from = 175_000.dollars, to = 200_000.dollars),
        TaxBracket(43_00.basisPoints, from = 200_000.dollars, to = 215_950.dollars),
        TaxBracket(46_00.basisPoints, from = 215_950.dollars, to = 539_900.dollars),
        TaxBracket(48_00.basisPoints, from = 539_900.dollars),
    )
}
