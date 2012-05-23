/*
 * Copyright 2012 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.linecount;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Hello world!
 *
 */
public class LineCountTransformer implements ClassFileTransformer
{

    private String prefix;

    public LineCountTransformer(String prefix) {
        this.prefix = prefix.replace('.','/');
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if(!className.startsWith(prefix)) {
            return null;
        }

        ClassReader reader = new ClassReader(classfileBuffer);

        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

        LineCountClassVisitor visitor = new LineCountClassVisitor(writer);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);

        Registry.registerClass(className, visitor.getExistingLines());

        return writer.toByteArray();
    }
}
