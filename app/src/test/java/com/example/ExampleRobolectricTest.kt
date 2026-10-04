package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ExcuseType
import com.example.network.FallbackExcuseGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Excuse Finder", appName)
  }

  @Test
  fun `fallback generator produces accurate situation responses`() {
    val result = FallbackExcuseGenerator.generateExcuses(
        situation = "I forgot to reply to my friend for 3 days.",
        category = "Messages",
        recipient = "Friend",
        severityLevel = 2,
        vibe = "Believable"
    )

    assertEquals(3, result.excuses.size)
    val safe = result.excuses.first { it.type == ExcuseType.SAFE }
    val funny = result.excuses.first { it.type == ExcuseType.FUNNY }
    val unhinged = result.excuses.first { it.type == ExcuseType.UNHINGED }

    assertTrue("Safe excuse should be non-empty", safe.text.isNotBlank())
    assertTrue("Funny excuse should be non-empty", funny.text.isNotBlank())
    assertTrue("Unhinged excuse should be non-empty", unhinged.text.isNotBlank())
    assertTrue("Safe believability should be high", safe.believability > 80)
    assertTrue("Unhinged risk should be high", unhinged.risk > 80)
  }

  @Test
  fun `excuse doctor diagnoses weak excuse correctly`() {
    val report = FallbackExcuseGenerator.diagnoseExcuse("Sorry I couldn't attend, my wifi was down.")
    assertNotNull(report)
    assertTrue(report.problem.isNotBlank())
    assertTrue(report.patchedVersion.isNotBlank())
    assertTrue(report.believability > 0)
  }
}
