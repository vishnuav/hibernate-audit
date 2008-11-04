package com.googlecode.hibernate.audit.listener;

import org.hibernate.event.SaveOrUpdateEventListener;
import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.apache.log4j.Logger;
import com.googlecode.hibernate.audit.model.Manager;
import com.googlecode.hibernate.audit.HibernateAuditException;

/**
 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 *
 * Copyright 2008 Ovidiu Feodorov
 *
 * @version <tt>$Revision$</tt>
 *
 * $Id$
 */
public class UpdateAuditEventListener
    extends AbstractAuditEventListener implements SaveOrUpdateEventListener
{
    // Constants -----------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SaveOrUpdateAuditEventListener.class);
    private static final boolean traceEnabled = log.isTraceEnabled();

    // Static --------------------------------------------------------------------------------------

    // Attributes ----------------------------------------------------------------------------------

    // Constructors --------------------------------------------------------------------------------

    public UpdateAuditEventListener(Manager m)
    {
        super(m);
    }

    // SaveOrUpdateEventListener implementation ----------------------------------------------------

    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
        try
        {
            if (traceEnabled) { log.trace(this + ".onSaveOrUpdate(" + event + ")"); }

            // this will create an audit transaction and properly register the synchronizations
            createAuditTransaction(event.getSession());
        }
        catch(Throwable t)
        {
            log.error("failed to log update event", t);

            try
            {
                Transaction tx = event.getSession().getTransaction();
                tx.rollback();
            }
            catch(Throwable t2)
            {
                log.error("could not rollback current transaction", t2);
            }

            // TODO bubble WriteCollisionException up https://jira.novaordis.org/browse/HBA-174
            throw new HibernateAuditException("failed to start audit transaction on update event",
                                              t);
        }
    }

    // Public --------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "UpdateAuditEventListener[" +
               Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected ---------------------------------------------------------------------------

    // Protected -----------------------------------------------------------------------------------

    // Private -------------------------------------------------------------------------------------

    // Inner classes -------------------------------------------------------------------------------

}
