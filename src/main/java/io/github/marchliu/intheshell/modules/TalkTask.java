package io.github.marchliu.intheshell.modules;

import jaskell.util.Try;
import javafx.concurrent.Task;

import java.util.concurrent.Future;

public class TalkTask extends Task<Response> {
    private final Future<Try<Response>> future;

    public TalkTask(Future<Try<Response>> future) {
        this.future = future;
    }

    @Override
    protected Response call() throws Exception {
        return future.get().get();
    }
}
