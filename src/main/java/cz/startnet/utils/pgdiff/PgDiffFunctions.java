/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgFunction;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.PrintWriter;

/**
 * Diffs functions.
 *
 * @author fordfrog
 */
public class PgDiffFunctions {

    /**
     * Creates a new instance of PgDiffFunctions.
     */
    private PgDiffFunctions() {
    }

    /**
     * Outputs statements for new or modified functions.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void createFunctions(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        // Add new functions and replace modified functions
        for (final PgFunction newFunction : newSchema.getFunctions()) {
            final PgFunction oldFunction;

            if (oldSchema == null) {
                oldFunction = null;
            } else {
                oldFunction = oldSchema.getFunction(newFunction.getSignature());
            }

            if ((oldFunction == null) || !newFunction.equals(
                    oldFunction, arguments.isIgnoreFunctionWhitespace())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(newFunction.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping of functions that exist no more.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void dropFunctions(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        // Drop functions that exist no more
        if (oldSchema != null) {
            for (final PgFunction oldFunction : oldSchema.getFunctions()) {
                if (!newSchema.containsFunction(oldFunction.getSignature())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.println(oldFunction.getDropSQL());
                }
            }
        }
    }
}
