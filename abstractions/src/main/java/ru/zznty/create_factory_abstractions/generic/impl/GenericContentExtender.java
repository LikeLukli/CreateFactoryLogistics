package ru.zznty.create_factory_abstractions.generic.impl;

import net.createmod.catnip.data.Pair;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.IForgeRegistry;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import ru.zznty.create_factory_abstractions.CreateFactoryAbstractions;
import ru.zznty.create_factory_abstractions.api.generic.extensibility.*;
import ru.zznty.create_factory_abstractions.api.generic.key.*;
import ru.zznty.create_factory_abstractions.generic.key.EmptyKey;
import ru.zznty.create_factory_abstractions.generic.key.EmptyKeyClientProvider;
import ru.zznty.create_factory_abstractions.generic.key.EmptyKeyProvider;
import ru.zznty.create_factory_abstractions.generic.key.EmptyKeySerializer;
import ru.zznty.create_factory_abstractions.generic.key.item.ItemKey;
import ru.zznty.create_factory_abstractions.generic.key.item.ItemKeyClientProvider;
import ru.zznty.create_factory_abstractions.generic.key.item.ItemKeyProvider;
import ru.zznty.create_factory_abstractions.generic.key.item.ItemKeySerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GenericContentExtender {
    private static final Map<String, GenericContentExtension> EXTENSIONS = new HashMap<>(); // <modId, extension>
    public static final String ID = "create_factory_abstractions";
    public static final ResourceKey<Registry<GenericKeyRegistration>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ID, "generic_keys"));
    public static Supplier<IForgeRegistry<GenericKeyRegistration>> REGISTRY;
    public static Map<Class<?>, GenericKeyRegistration> REGISTRATIONS = new HashMap<>(); // <key, provider>

    @ApiStatus.Internal
    public static void enqueueExtension(String modId, GenericContentExtension extension) {
        if (CreateFactoryAbstractions.EXTENSIBILITY_AVAILABLE)
            EXTENSIONS.putIfAbsent(modId, extension);
    }

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        bus.register(GenericContentExtender.class);
    }

    public static GenericKeyRegistration registrationOf(GenericKey instance) {
        return REGISTRATIONS.get(instance.getClass());
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onRegistry(NewRegistryEvent event) {
        REGISTRY = event.create(new RegistryBuilder<>(REGISTRY_KEY)
                                        .defaultKey(ResourceLocation.fromNamespaceAndPath(ID, "empty"))
                                        .sync(false),
                                GenericContentExtender::fillKeys);
    }

    private static void fillKeys(IForgeRegistry<GenericKeyRegistration> registry) {
        Registration<EmptyKey> emptyKeyRegistration = new Registration<>(new EmptyKeyProvider(),
                                                                         new EmptyKeySerializer(),
                                                                         FMLEnvironment.dist.isClient() ? new EmptyKeyClientProvider() : null);
        registry.register(ResourceLocation.fromNamespaceAndPath(ID, "empty"),
                          emptyKeyRegistration);
        REGISTRATIONS.put(EmptyKey.class, emptyKeyRegistration);

        Registration<ItemKey> itemKeyRegistration = new Registration<>(new ItemKeyProvider(), new ItemKeySerializer(),
                                                                       FMLEnvironment.dist.isClient() ? new ItemKeyClientProvider() : null);
        registry.register(ResourceLocation.fromNamespaceAndPath(ID, "item"),
                          itemKeyRegistration);
        REGISTRATIONS.put(ItemKey.class, itemKeyRegistration);
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onRegisterExtensions(RegisterEvent event) {
        event.register(REGISTRY.get().getRegistryKey(), helper -> {
            for (Map.Entry<String, GenericContentExtension> entry : EXTENSIONS.entrySet()) {
                GenericContentExtension extension = entry.getValue();
                Map<String, Pair<CommonRegistrationBuilderImpl<?>, ClientRegistrationBuilderImpl<?>>> registrations = new HashMap<>();
                Map<String, Class<?>> keyClasses = new HashMap<>();
                extension.registerCommon(new CommonContentRegistration() {
                    @Override
                    public <Key extends GenericKey> void register(String id,
                                                                  Class<Key> keyClass,
                                                                  Consumer<CommonRegistrationBuilder<Key>> builder) {
                        CommonRegistrationBuilderImpl<Key> registrationBuilder = new CommonRegistrationBuilderImpl<>(
                                keyClass);
                        builder.accept(registrationBuilder);
                        registrations.putIfAbsent(id, Pair.of(registrationBuilder, null));
                        keyClasses.putIfAbsent(id, keyClass);
                    }
                });

                if (FMLEnvironment.dist.isClient()) {
                    extension.registerClient(new ClientContentRegistration() {
                        @Override
                        public <Key extends GenericKey> void register(String id,
                                                                      Consumer<ClientRegistrationBuilder<Key>> builder) {
                            ClientRegistrationBuilderImpl<Key> registrationBuilder = new ClientRegistrationBuilderImpl<>();
                            builder.accept(registrationBuilder);
                            registrations.computeIfPresent(id,
                                                           (s, pair) -> Pair.of(pair.getFirst(),
                                                                                registrationBuilder));
                        }
                    });
                }

                for (Map.Entry<String, Pair<CommonRegistrationBuilderImpl<?>, ClientRegistrationBuilderImpl<?>>> pairEntry : registrations.entrySet()) {
                    Pair<CommonRegistrationBuilderImpl<?>, ClientRegistrationBuilderImpl<?>> pair = pairEntry.getValue();
                    Pair<? extends GenericKeyProvider<?>, ? extends GenericKeySerializer<?>> commonPair = pair.getFirst().build();
                    ClientRegistrationBuilderImpl<?> clientRegistrationBuilder = pair.getSecond();
                    //noinspection rawtypes,unchecked
                    GenericKeyRegistration registration = new Registration(commonPair.getFirst(),
                                                                           commonPair.getSecond(),
                                                                           clientRegistrationBuilder == null ?
                                                                           null
                                                                                                             :
                                                                           clientRegistrationBuilder.build());
                    helper.register(ResourceLocation.fromNamespaceAndPath(entry.getKey(), pairEntry.getKey()),
                                    registration);

                    Class<?> keyClass = keyClasses.get(pairEntry.getKey());
                    REGISTRATIONS.put(keyClass, registration);
                }
            }
        });
    }

    private record Registration<Key extends GenericKey>(GenericKeyProvider<Key> keyProvider,
                                                        GenericKeySerializer<Key> keySerializer,
                                                        @Nullable GenericKeyClientProvider<Key> clientKeyProvider) implements GenericKeyRegistration {

        @Override
        public <K extends GenericKey> GenericKeyProvider<K> provider() {
            //noinspection unchecked
            return (GenericKeyProvider<K>) keyProvider;
        }

        @Override
        public <K extends GenericKey> GenericKeySerializer<K> serializer() {
            //noinspection unchecked
            return (GenericKeySerializer<K>) keySerializer;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public <K extends GenericKey> GenericKeyClientProvider<K> clientProvider() {
            //noinspection unchecked
            return (GenericKeyClientProvider<K>) clientKeyProvider;
        }
    }
}
