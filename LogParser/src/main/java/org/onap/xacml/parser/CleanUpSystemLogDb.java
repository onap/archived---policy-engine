/*-
 * ============LICENSE_START=======================================================
 * LogParser
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */


package org.onap.xacml.parser;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.onap.policy.common.logging.flexlogger.FlexLogger;

public class CleanUpSystemLogDb extends TimerTask {

    private static org.onap.policy.common.logging.flexlogger.Logger logger =
            FlexLogger.getLogger(CleanUpSystemLogDb.class.getName());
    Connection localConnect = null;
    int timeFrame = 5; // default

    /**
     * Set time frame for database cleanup.
     *
     * @param dbConnect the database connection object
     * @param argTimeFrame the time frame
     */
    public CleanUpSystemLogDb(final Connection dbConnect, final int argTimeFrame) {
        localConnect = dbConnect;
        if (argTimeFrame > 0) {
            timeFrame = argTimeFrame;
        }
    }

    String className = this.getClass().getSimpleName();

    @Override
    public void run() {

        final Date date = new Date();
        final Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.debug("cleanLogDBTableEntries:Cleanup systemlogdb starts on date:" + formatter.format(date));
        try {
            cleanLogDbTableEntries(localConnect, timeFrame);
        } catch (final SQLException e) {
            logger.error(e);
        }

        logger.debug(className + " Cleanup systemlogdb done");
    }

    /**
     * Clean system log database table entries based on input time frame.
     *
     * @param dbConnect the database connection object
     * @param timeFrame the time frame
     * @throws SQLException if an error occurs
     */
    public static void cleanLogDbTableEntries(final Connection dbConnect, final int timeFrame) throws SQLException {

        Connection connect = dbConnect;
        if (dbConnect == null || dbConnect.isClosed()) {
            connect = ParseLog.getDbConnection();
        }
        try (java.sql.PreparedStatement statement =
                connect.prepareStatement("DELETE FROM SYSTEMLOGDB WHERE date < DATE_SUB(CURDATE(), INTERVAL ? DAY)");) {

            statement.setInt(1, timeFrame);

            final int records = statement.executeUpdate();

            logger.debug("cleanLogDBTableEntries:deleting Log files ended with " + records + " deleted.");
            statement.close();

        } catch (final Exception e) {
            logger.error("Failed to create SQLContainer for System Log Database", e);
        } finally {
            connect.close();
        }
    }
}
