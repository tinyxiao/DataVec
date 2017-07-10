package org.datavec.api.pipelines.functions.abstracts;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.datavec.api.pipelines.api.Function;
import org.datavec.api.pipelines.api.PipelineFunction;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author raver119@gmail.com
 */
@Slf4j
public abstract class AbstractFunction<IN> implements PipelineFunction<IN>, Function<IN> {
    protected PipelineFunction<IN> prevFunction;
    protected PipelineFunction<IN> nextFunction;
    protected Queue<IN> queue = new LinkedTransferQueue<>();


    @Override
    public IN execute(IN input) {
        log.info("Execute on input [{}]", input);
        if (nextFunction != null)
            return nextFunction.execute(call(input));
        else return call(input);
    }

    @Override
    public IN poll() {
        // TODO: maybe something better then typecast here?

        /*
            1) check out queue
            2) check parent queue
            3) return null
         */
        if (!queue.isEmpty())
            return queue.poll();

        if (prevFunction !=null)
            return prevFunction.poll();

        return null;
    }

    @Override
    public void attachPrev(@NonNull PipelineFunction function) {
        this.prevFunction = function;
    }

    @Override
    public void attachNext(@NonNull PipelineFunction function) {
        this.nextFunction = function;
    }

    /**
     * This method goes backwards
     * @return
     */
    @Override
    public boolean hasNext() {
        if (!queue.isEmpty())
            return true;

        return prevFunction == null ? false : prevFunction.hasNext();
    }
}
