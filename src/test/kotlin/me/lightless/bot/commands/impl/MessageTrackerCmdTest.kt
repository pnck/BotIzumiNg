package me.lightless.bot.commands.impl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class MessageTrackerCmdTest {
    @Test
    fun testMessageTrackerCmd() {
        val cmd = MessageTrackerCmd()
        val ctx = cmd.entries("/tracker").context() as MessageTrackerCmd.MessageTrackerContext
        Assertions.assertEquals("MessageTracker", ctx.scope)
        // Assertions.assertEquals(1, cmd.ctx.v2)
        cmd.entries("test").execute()
    }
}