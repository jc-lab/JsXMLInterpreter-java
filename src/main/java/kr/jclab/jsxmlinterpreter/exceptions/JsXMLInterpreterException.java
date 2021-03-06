/*
 * Copyright 2018 JC-Lab. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.jclab.jsxmlinterpreter.exceptions;

public class JsXMLInterpreterException extends Exception {

    public JsXMLInterpreterException() {
        super();
    }

    public JsXMLInterpreterException(String message) {
        super(message);
    }

    public JsXMLInterpreterException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsXMLInterpreterException(Throwable cause) {
        super(cause);
    }

    protected JsXMLInterpreterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
