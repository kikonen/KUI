package org.kari.util.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.kari.util.log.LogUtil;

/**
 * Async refresher
 * 
 * <p>USAGE:
 * <pre>
 * class Foo {
 *     Refresher<Bar> mBarRefresher = new Refresher<Bar>() {
 *     }
 *     
 *     synchronized void zoo() {
 *         mBarRefresher = Refresher.kill(mBar);
 *         mBarRefresher = new Refresher() {
 *         };
 *     }
 * }
 * </pre>
 * 
 * @author kari
 */
public abstract class Refresher <T_Data> {
    final class Operation implements Callable<Object> {
        private T_Data mData;
        private Future<Object> mFuture;

        public Operation(T_Data pData) {
            mData = pData;
        }
        
        public void setFuture(Future<Object> pFuture) {
            mFuture = pFuture;
        }

        @Override
        public Object call() throws Exception {
            try {
                Thread.sleep(mDelay);
                
                Object constructed = null;
                if (!mFuture.isCancelled()) {
                    constructed = construct(mData);
                }
                if (!mFuture.isCancelled()) {
                    finish(mData, constructed);
                }
                return constructed;
            } catch (InterruptedException e) {
                // this in not "failure"
                throw e;
            } catch (Exception e) {
                failed(e,  mData);
                throw e;
            } finally {
                mData = null;
                mFuture = null;
            }
        }
    }
    
    protected static final Logger LOG = LogUtil.getLogger("refresher");
    
    private final ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(
            0, 
            1, 
            (long)2000, 
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(1));

    private final int mDelay;
    private Future<Object> mCurrent;

    
    /**
     * @param pDelay Delay for refresh in ms
     */
    public Refresher(int pDelay) {
        mDelay = pDelay;
        mExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * Kill refresher
     */
    public synchronized void kill() {
        cancel();
        mExecutor.shutdownNow();
    }

    /**
     * Cancel current refresh
     */
    public synchronized void cancel() {
        if (mCurrent != null) {
            mCurrent.cancel(true);
            mCurrent = null;
        }
    }

    /**
     * Start new refresh, and kill possibly pending refresh
     */
    public synchronized void start(T_Data pData) {
        cancel();
        Operation operation = new Operation(pData);
        mCurrent = mExecutor.submit(operation);
        operation.setFuture(mCurrent);
    }
    
    /**
     * Construct content
     * 
     * @return null by default
     */
    protected Object construct(T_Data pData)
        throws Exception
    {
        // Nothing
        return null;
    }

    /**
     * Finish; invoked only if operation was not cancelled
     */
    protected abstract void finish(
            T_Data pData,
            Object pConstructed)
        throws Exception;

    /**
     * Refresh failed
     */
    protected void failed(
            Exception pError, 
            T_Data pData) 
    {
        LOG.error("refresher failed: " + pData, pError);
    }

}
